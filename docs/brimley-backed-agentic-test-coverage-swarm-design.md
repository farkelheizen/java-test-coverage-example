# Brimley-Backed Agentic Test Coverage Swarm — Design

> Status: Design proposal. Brimley tools and database are local-only; VS Code agents coordinate via MCP tool calls against a Brimley daemon.

## 1. Problem Statement

The current swarm (`coverage-coordinator` → `jacoco-coverage-analyst` → `java-test-writer`) works well for single-session runs, but every piece of state — which classes need tests, who is working on what, what coverage was achieved — lives only inside a single conversation's context window. That means:

- No durable record of what was attempted or completed across runs.
- No safe way for multiple parallel agents to claim disjoint work without collisions.
- No way to resume a partially completed run after a session ends.
- Coverage metrics must be re-parsed from JaCoCo XML on every invocation.

**Goal:** Replace in-context bookkeeping with a local SQLite database managed by Brimley MCP tools. Separate seeding (run setup + JaCoCo import) from execution (test writing), so a single seed populates the work queue and any number of concurrent worker agents can pull from it throughout the day.

This local-only design uses `brimley-*` agent names to distinguish it from the earlier cloud-oriented `coverage-*`, `jacoco-*`, and `java-test-writer` agents that remain in the repository for reference.

See also:

- [Implementation Plan](./brimley-code-coverage-swarm-implementation-plan.md)
- [Launch Guide](./how-to-kick-off-the-autonomous-swarm.md)

---

## 2. Architecture Overview

The system separates seeding from execution. A **seeder** agent runs once to populate the work queue, then any number of **worker** agents pull from it throughout the day. All agents talk to one shared Brimley daemon over MCP; only the daemon touches SQLite.

```
┌───────────────────────────────────────────────────────────────────┐
│  VS Code Chat                                                     │
│                                                                   │
│  ┌─────────────────────┐       (run once, then done)              │
│  │ brimley-coverage-   │─────────────────────┐                    │
│  │ seeder              │                     │                    │
│  └─────────────────────┘                     │                    │
│                                              │ MCP calls          │
│  ┌─────────────────────┐                     │                    │
│  │ brimley-coverage-   │──────┐              │                    │
│  │ worker              │      │              │                    │
│  │  (session A)        │      │              │                    │
│  └─────────────────────┘      │              │                    │
│                               │              │                    │
│  ┌─────────────────────┐      │ MCP calls    │                    │
│  │ brimley-coverage-   │──────┤              │                    │
│  │ worker              │      │              │                    │
│  │  (session B)        │      │              │                    │
│  └─────────────────────┘      │              │                    │
│                               │              │                    │
│  ┌─────────────────────┐      │              │                    │
│  │ brimley-coverage-   │──────┤              │                    │
│  │ worker              │      │              │                    │
│  │  (session C)        │      │              │                    │
│  └─────────────────────┘      │              │                    │
│                               │              │                    │
└───────────────────────────────┼──────────────┼────────────────────┘
                                │              │
                                ▼              ▼
┌────────────────────────────────────────────────────────────────────┐
│  Brimley Daemon (local, SSE transport)                             │
│                                                                    │
│  ┌────────────────────────────────────────────-─┐                  │
│  │  MCP Tools (Python + SQL functions)          │                  │
│  │                                              │                  │
│  │  Seeder tools        Worker tools            │                  │
│  │  ─────────────       ────────────            │                  │
│  │  create_run          list_uncovered_classes  │                  │
│  │  import_jacoco       checkout_classes        │                  │
│  │  close_run           complete_class          │                  │
│  │  get_run_summary     fail_class              │                  │
│  │                      release_class           │                  │
│  │                      get_run_summary         │                  │
│  └──────────────────────┬─────────────────────-─┘                  │
│                         │                                          │
│                         ▼                                          │
│                 ┌───────────────┐                                  │
│                 │  SQLite DB    │                                  │
│                 │  swarm.db     │                                  │
│                 └───────────────┘                                  │
└────────────────────────────────────────────────────────────────────┘
```

**Key properties:**

- One Brimley daemon runs locally (`brimley repl --root .` or `brimley mcp-serve --root .`) for the active swarm.
- All VS Code agents connect to that same daemon as an MCP server (SSE at `http://127.0.0.1:8000/sse`).
- All state is in one shared `swarm.db` owned by the daemon — durable across sessions, inspectable with any SQLite client.
- Agents never write to the database directly; they only call MCP tools exposed by Brimley.
- Git worktrees isolate source edits and build artifacts, not coordination state.
- **Seeder runs once** — creates a run, builds JaCoCo, imports the report, then exits.
- **Workers are stateless** — each one queries for pending work, claims it, writes tests, and reports results. Launch as many as you want.

---

## 3. SQLite Database Schema

Database file: `swarm.db` (configured in `brimley.yaml` under `databases.default` and accessed only through Brimley MCP tools).

### 3.1 `runs` — Top-Level Orchestration Runs

Each coordinator invocation creates one run. Tracks scope, limits, and aggregate results.

```sql
CREATE TABLE IF NOT EXISTS runs (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    name            TEXT NOT NULL,               -- human label, e.g. "service-pkg-run-1"
    target_scope    TEXT NOT NULL,               -- e.g. "com.example.service"
    class_limit     INTEGER NOT NULL DEFAULT 5,  -- max classes for this run
    coverage_target REAL,                        -- e.g. 0.70 for 70%
    status          TEXT NOT NULL DEFAULT 'open' -- open | completed | aborted
                        CHECK (status IN ('open', 'completed', 'aborted')),
    created_at      TEXT NOT NULL DEFAULT (datetime('now')),
    closed_at       TEXT,
    notes           TEXT                         -- free-form coordinator notes
);
```

### 3.2 `classes` — Per-Class Coverage Tracking

One row per Java class discovered in a JaCoCo report import. Represents the work queue.

```sql
CREATE TABLE IF NOT EXISTS classes (
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    run_id              INTEGER NOT NULL REFERENCES runs(id),
    fqcn                TEXT NOT NULL,               -- e.g. "com.example.service.OrderService"
    package_name        TEXT NOT NULL,               -- e.g. "com.example.service"
    source_file         TEXT,                        -- relative path to .java
    -- JaCoCo metrics at import time
    instruction_missed  INTEGER NOT NULL DEFAULT 0,
    instruction_covered INTEGER NOT NULL DEFAULT 0,
    branch_missed       INTEGER NOT NULL DEFAULT 0,
    branch_covered      INTEGER NOT NULL DEFAULT 0,
    line_missed         INTEGER NOT NULL DEFAULT 0,
    line_covered        INTEGER NOT NULL DEFAULT 0,
    complexity_missed   INTEGER NOT NULL DEFAULT 0,
    complexity_covered  INTEGER NOT NULL DEFAULT 0,
    -- Derived convenience columns (populated by import tool)
    instruction_coverage REAL,                       -- 0.0–1.0
    branch_coverage      REAL,                       -- 0.0–1.0
    has_existing_test    INTEGER NOT NULL DEFAULT 0, -- 1 if a test file exists
    -- Work state
    status              TEXT NOT NULL DEFAULT 'pending'
                            CHECK (status IN (
                                'pending',       -- not yet claimed
                                'checked_out',   -- agent is working on it
                                'completed',     -- tests written and passing
                                'failed',        -- agent gave up or tests broken
                                'skipped'        -- excluded by coordinator
                            )),
    checked_out_by      TEXT,                        -- agent/session identifier
    checked_out_at      TEXT,
    completed_at        TEXT,
    -- Post-test metrics (filled after re-running JaCoCo)
    post_instruction_coverage REAL,
    post_branch_coverage      REAL,
    test_file                 TEXT,                   -- path to generated test
    error_message             TEXT,                   -- failure reason if status='failed'
    UNIQUE(run_id, fqcn)
);
```

### 3.3 `events` — Audit Log

Append-only log of every state transition for debugging and resumability.

```sql
CREATE TABLE IF NOT EXISTS events (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    run_id      INTEGER NOT NULL REFERENCES runs(id),
    class_id    INTEGER REFERENCES classes(id),
    event_type  TEXT NOT NULL,                       -- e.g. 'import', 'checkout', 'complete', 'fail', 'release'
    agent_id    TEXT,                                -- who triggered the event
    detail      TEXT,                                -- JSON or free-form detail
    created_at  TEXT NOT NULL DEFAULT (datetime('now'))
);
```

### 3.4 Index Recommendations

```sql
CREATE INDEX IF NOT EXISTS idx_classes_run_status ON classes(run_id, status);
CREATE INDEX IF NOT EXISTS idx_classes_run_fqcn   ON classes(run_id, fqcn);
CREATE INDEX IF NOT EXISTS idx_events_run         ON events(run_id);
CREATE INDEX IF NOT EXISTS idx_events_class       ON events(class_id);
```

---

## 4. Brimley MCP Tools

Each tool below is exposed as an MCP tool (`mcpType="tool"` or `mcp: { type: tool }`). The Brimley daemon handles argument validation, database connections, and result formatting.

### 4.1 `create_run` — Start a New Orchestration Run

**Type:** SQL function  
**Purpose:** Creates a `runs` row so subsequent imports and checkouts are scoped.

```sql
/*
---
name: create_run
type: template_function
return_shape: object
arguments:
  inline:
    name:
      type: string
    target_scope:
      type: string
    class_limit:
      type: integer
      default: 5
    coverage_target:
      type: number
      default: 0.70
database: default
mcp:
  type: tool
---
*/
INSERT INTO runs (name, target_scope, class_limit, coverage_target)
VALUES (:name, :target_scope, :class_limit, :coverage_target);
SELECT id AS run_id, name, status FROM runs WHERE id = last_insert_rowid();
```

**Returns:** `{ "run_id": 1, "name": "...", "status": "open" }`

---

### 4.2 `import_jacoco_report` — Parse JaCoCo XML Into the Database

**Type:** Python function (needs XML parsing — not suitable for pure SQL)  
**Purpose:** Reads `target/site/jacoco/jacoco.xml`, extracts per-class metrics, and upserts rows into `classes` for a given `run_id`.

```python
@function(name="import_jacoco_report", mcpType="tool")
def import_jacoco_report(
    run_id: int,
    report_path: str = "target/site/jacoco/jacoco.xml",
    scope_filter: str = "",
    ctx: BrimleyContext = None,
) -> dict:
    """Parses a JaCoCo XML report and imports class-level metrics.

    scope_filter narrows to classes whose FQCN starts with this prefix.
    Returns the count of classes imported.
    """
```

**Logic:**

1. Parse the JaCoCo XML (`<package>` → `<class>` → `<counter>` elements).
2. For each class matching `scope_filter`, compute coverage ratios.
3. Check whether a matching test file exists under `src/test/java/`.
4. INSERT OR REPLACE into `classes`.
5. Log an `import` event.

**Returns:** `{ "run_id": 1, "classes_imported": 24, "scope_filter": "com.example.service" }`

---

### 4.3 `list_uncovered_classes` — Query the Work Queue

**Type:** SQL function (pure read query — ideal for `.sql` file)  
**Purpose:** Returns pending classes ordered by priority (most uncovered first).

```sql
/*
---
name: list_uncovered_classes
type: sql_function
description: >
  Lists classes in a run that are still pending, ordered by lowest
  instruction coverage first. Use this to decide what to check out.
connection: default
arguments:
  inline:
    run_id:
      type: int
    limit:
      type: int
      default: 10
    min_missed_instructions:
      type: int
      default: 0
return_shape: list[dict]
mcp:
  type: tool
---
*/
SELECT
    id,
    fqcn,
    package_name,
    instruction_missed,
    instruction_covered,
    branch_missed,
    branch_covered,
    instruction_coverage,
    branch_coverage,
    has_existing_test,
    status
FROM classes
WHERE run_id = :run_id
  AND status = 'pending'
  AND instruction_missed >= :min_missed_instructions
ORDER BY instruction_coverage ASC, instruction_missed DESC
LIMIT :limit;
```

---

### 4.4 `checkout_classes` — Claim Classes for an Agent

**Type:** Python function (needs transaction + multi-row update)  
**Purpose:** Atomically marks 1–N pending classes as `checked_out` so no other agent picks them up.

```python
@function(name="checkout_classes", mcpType="tool")
def checkout_classes(
    run_id: int,
    class_ids: list[int],
    agent_id: str,
    ctx: BrimleyContext = None,
) -> dict:
    """Claims the specified classes for an agent.

    Only classes with status='pending' in the given run will be checked out.
    Returns the list of successfully claimed class FQCNs.
    """
```

**Logic:**

1. Begin transaction.
2. `UPDATE classes SET status='checked_out', checked_out_by=:agent_id, checked_out_at=datetime('now') WHERE id IN (:ids) AND run_id=:run_id AND status='pending'`.
3. Return the list of actually updated rows (handles races gracefully).
4. Log `checkout` events.

**Returns:** `{ "checked_out": ["com.example.service.OrderService", ...], "count": 3 }`

---

### 4.5 `complete_class` — Mark a Class as Done

**Type:** SQL function  
**Purpose:** Transitions a class to `completed` with optional post-test metrics.

```sql
/*
---
name: complete_class
type: sql_function
description: >
  Marks a checked-out class as completed with optional post-test coverage.
connection: default
arguments:
  inline:
    class_id:
      type: int
    test_file:
      type: str
    post_instruction_coverage:
      type: float
      default: null
    post_branch_coverage:
      type: float
      default: null
return_shape: dict
mcp:
  type: tool
---
*/
UPDATE classes
SET status = 'completed',
    completed_at = datetime('now'),
    test_file = :test_file,
    post_instruction_coverage = :post_instruction_coverage,
    post_branch_coverage = :post_branch_coverage
WHERE id = :class_id
  AND status = 'checked_out'
RETURNING id, fqcn, status, test_file;
```

---

### 4.6 `fail_class` — Record a Failure

**Type:** SQL function  
**Purpose:** Records that test generation failed for a class.

```sql
/*
---
name: fail_class
type: sql_function
description: >
  Marks a checked-out class as failed with an error message.
connection: default
arguments:
  inline:
    class_id:
      type: int
    error_message:
      type: str
return_shape: dict
mcp:
  type: tool
---
*/
UPDATE classes
SET status = 'failed',
    completed_at = datetime('now'),
    error_message = :error_message
WHERE id = :class_id
  AND status = 'checked_out'
RETURNING id, fqcn, status, error_message;
```

---

### 4.7 `release_class` — Return a Class to the Queue

**Type:** SQL function  
**Purpose:** Unclaims a checked-out class so another agent can pick it up (e.g., after a timeout or voluntary release).

```sql
/*
---
name: release_class
type: sql_function
description: >
  Returns a checked-out class to 'pending' so another agent can claim it.
connection: default
arguments:
  inline:
    class_id:
      type: int
return_shape: dict
mcp:
  type: tool
---
*/
UPDATE classes
SET status = 'pending',
    checked_out_by = NULL,
    checked_out_at = NULL
WHERE id = :class_id
  AND status = 'checked_out'
RETURNING id, fqcn, status;
```

---

### 4.8 `get_run_summary` — Dashboard View

**Type:** SQL function  
**Purpose:** Aggregate status counts and coverage metrics for a run.

```sql
/*
---
name: get_run_summary
type: sql_function
description: >
  Returns aggregate progress for a run: counts by status and average coverage.
connection: default
arguments:
  inline:
    run_id:
      type: int
return_shape: dict
mcp:
  type: tool
---
*/
SELECT
    r.id AS run_id,
    r.name,
    r.status AS run_status,
    r.class_limit,
    r.coverage_target,
    COUNT(c.id) AS total_classes,
    SUM(CASE WHEN c.status = 'pending' THEN 1 ELSE 0 END) AS pending,
    SUM(CASE WHEN c.status = 'checked_out' THEN 1 ELSE 0 END) AS checked_out,
    SUM(CASE WHEN c.status = 'completed' THEN 1 ELSE 0 END) AS completed,
    SUM(CASE WHEN c.status = 'failed' THEN 1 ELSE 0 END) AS failed,
    SUM(CASE WHEN c.status = 'skipped' THEN 1 ELSE 0 END) AS skipped,
    ROUND(AVG(c.instruction_coverage), 4) AS avg_instruction_coverage,
    ROUND(AVG(c.branch_coverage), 4) AS avg_branch_coverage,
    ROUND(AVG(c.post_instruction_coverage), 4) AS avg_post_instruction_coverage,
    ROUND(AVG(c.post_branch_coverage), 4) AS avg_post_branch_coverage
FROM runs r
LEFT JOIN classes c ON c.run_id = r.id
WHERE r.id = :run_id
GROUP BY r.id;
```

---

### 4.9 `close_run` — Finalize a Run

**Type:** SQL function  
**Purpose:** Marks a run as completed or aborted.

```sql
/*
---
name: close_run
type: sql_function
description: >
  Closes a run with a final status of 'completed' or 'aborted'.
connection: default
arguments:
  inline:
    run_id:
      type: int
    status:
      type: str
      default: "completed"
    notes:
      type: str
      default: ""
return_shape: dict
mcp:
  type: tool
---
*/
UPDATE runs
SET status = :status,
    closed_at = datetime('now'),
    notes = :notes
WHERE id = :run_id
  AND status = 'open'
RETURNING id, name, status, closed_at;
```

---

## 5. Brimley Project Layout

All files live in a dedicated directory (e.g., `brimley-swarm/` or integrated into the existing `brimley/examples/` directory).

```
brimley-swarm/
├── brimley.yaml                   # Brimley config pointing to swarm.db
├── init_db.py                     # Brimley @on_startup database initialization
├── import_jacoco_report.py        # Python MCP tool: XML → SQLite
├── checkout_classes.py            # Python MCP tool: atomic checkout
├── create_run.sql                 # SQL MCP tool: create a run
├── list_uncovered_classes.sql     # SQL MCP tool: query pending work
├── complete_class.sql             # SQL MCP tool: mark done
├── fail_class.sql                 # SQL MCP tool: mark failed
├── release_class.sql              # SQL MCP tool: unclaim
├── get_run_summary.sql            # SQL MCP tool: dashboard
├── close_run.sql                  # SQL MCP tool: finalize
└── swarm.db                       # Shared SQLite database owned by the Brimley daemon
```

If workers operate from separate git worktrees, keep this database at a single shared path owned by the daemon. Do not start a separate per-worktree database unless you intentionally want isolated runs.

### `brimley.yaml`

```yaml
brimley:
  app_name: "Test Coverage Swarm"
  log_level: "INFO"

config:
  project_root: "." # path to the Java project
  jacoco_report: "target/site/jacoco/jacoco.xml"
  test_source_root: "src/test/java"
  main_source_root: "src/main/java"

state:
  active_run_id: null

databases:
  default:
    connector: sqlite
    url: "sqlite:///./swarm.db" # one shared DB for the daemon; use a stable path in worktree-based setups

mcp:
  embedded: true
  host: 127.0.0.1
  port: 8000

auto_reload:
  enabled: true
```

---

## 6. Agentic Workflow — VS Code Integration

### 6.1 How Agents Connect to the Brimley MCP Server

VS Code's MCP client configuration (in `.vscode/mcp.json` or user settings) registers the Brimley SSE endpoint:

```jsonc
// .vscode/mcp.json
{
  "servers": {
    "brimley-coverage-swarm": {
      "type": "sse",
      "url": "http://127.0.0.1:8000/sse",
    },
  },
}
```

Once registered, every VS Code agent can call the Brimley tools by name through that same endpoint — they appear as MCP tools in the agent's tool list alongside the built-in VS Code tools. In this repository, that MCP server is named `brimley-coverage-swarm`. Workers may sit in different chat sessions or different git worktrees, but they still coordinate through the same Brimley server.

### 6.2 Agent Roles

The local Brimley agents live in `.github/agents/` under the `brimley-*` prefix. They are separate from the older cloud-oriented swarm agents.

#### `brimley-coverage-seeder` (run once)

The seeder is a short-lived agent that populates the work queue. Run it once at the start of a coverage push (e.g., top of the day):

```
1. Call `create_run(name, target_scope, class_limit, coverage_target)`
   → receives run_id

2. Run `mvn clean test jacoco:report` via terminal

3. Call `import_jacoco_report(run_id, scope_filter=target_scope)`
   → populates the classes table from JaCoCo XML

4. Call `get_run_summary(run_id)` to confirm the work queue is populated

5. Print the run_id and summary — done.
```

The seeder **does not write tests**. It builds the backlog and exits. You can re-run the seeder later with a different `target_scope` or `class_limit` to create additional runs.

#### `brimley-coverage-worker` (run many, concurrently)

Each worker is an autonomous agent that pulls work from the queue and writes tests. Launch as many as you want across separate chat sessions — the checkout mechanism prevents queue collisions. For file-system isolation, run each worker in its own git worktree and, preferably, on its own branch.

**Optional parameters:**
- `run_id` — which run to pull from (defaults to the most recent open run)
- `count` — how many classes to claim per cycle (default: 3)
- `package_filter` — restrict to classes in a specific package (e.g., `com.example.service`)
- `fqcn_list` — explicit list of fully qualified class names to target
- `worktree_path` — optional dedicated git worktree for this worker
- `branch_name` — optional unique branch name for that worktree

```
1. Call `get_run_summary(run_id)` to see current state
   → if no run_id given, find the latest open run

2. Call `list_uncovered_classes(run_id, limit=count)`
   → optionally filter by package or FQCN
   → gets the next batch of pending classes

3. Call `checkout_classes(run_id, class_ids, agent_id=<session_id>)`
   → atomically claims the classes; races return empty results

4. For each checked-out class:
   a. Read the source file
   b. Write a JUnit 5 + Mockito test
   c. Run `mvn test` to validate
   d. If tests pass: `complete_class(class_id, test_file, ...)`
   e. If tests fail after retries: `fail_class(class_id, error_message)`

5. Re-run `mvn test jacoco:report` and call `get_run_summary(run_id)`
   → report progress

6. If pending classes remain and the worker has capacity, loop back to step 2

7. When no pending classes remain (or coverage target is met):
   → print summary and exit
```

> **Design choice:** Workers are self-contained — they call Brimley tools directly, read source, write tests, and report results. No subagent delegation is required for the default flow, though `brimley-jacoco-coverage-analyst` remains available as an optional read-only helper. This keeps each chat session simple and independently resumable.

#### `brimley-coverage-worker-isolated` (recommended for parallel editing)

This variant uses the same shared Brimley daemon and queue, but performs source edits from a dedicated git worktree for the worker session.

**Additional optional parameters:**
- `worktree_path` — where to create or reuse the worker worktree
- `branch_name` — unique branch for that worktree
- `base_ref` — git ref to branch from, default `HEAD`
- `reuse_worktree` — whether an existing worktree may be reused

```
1. Ensure the shared `brimley-coverage-swarm` MCP server is already running.

2. Create or reuse a dedicated git worktree for the worker.
  → optionally create a unique branch for that worktree

3. If the current chat session cannot continue from the new worktree automatically:
  → stop and print the exact `cd` command and exact rerun prompt

4. From inside that worktree, follow the normal `brimley-coverage-worker` flow:
  → `get_run_summary` → `list_uncovered_classes` → `checkout_classes`
  → write tests → run Maven → `complete_class` or `fail_class`

5. If post-test coverage should be imported back into the database:
  → call `import_jacoco_report` with the report path from that worker's worktree
```

This keeps queue coordination centralized while isolating uncommitted file changes between workers.

#### `brimley-jacoco-coverage-analyst` (optional helper)

With coverage data in SQLite, explicit analysis is often unnecessary. However, the analyst remains useful when a worker wants:

- Recommendations on which edge cases to target per class
- Complex prioritization logic that benefits from reading source code
- Package-level strategy before a batch of work

### 6.3 Parallel Worker Execution

Multiple workers safely share a single run thanks to atomic checkout at the shared Brimley daemon. When workers also use separate git worktrees, they avoid colliding in the working tree while still drawing from the same queue:

```
                    brimley-coverage-seeder (morning)
                    ─────────────────────────
                    create_run(scope="com.example", class_limit=50)
                    import_jacoco_report(run_id=1)
                    "Run 1 seeded with 47 classes. Go."
                    (exits)

Session A                          Session B                         Session C
─────────                          ─────────                         ─────────
brimley-coverage-worker-isolated   brimley-coverage-worker-isolated  brimley-coverage-worker-isolated
  run_id=1, count=3                  run_id=1, count=5                 run_id=1,
  package=com.example.service        package=com.example.util          count=3
  worktree=../cov-service-a         worktree=../cov-util-b            worktree=../cov-mixed-c
  branch=worker/run-1-a             branch=worker/run-1-b             branch=worker/run-1-c

list → [3,5,7]                     list → [12,14,16,18,20]           list → [22,24,26]
checkout → claims 3,5,7            checkout → claims 12,14,16,18,20  checkout → claims 22,24,26
write tests...                     write tests...                    write tests...
complete(3), complete(5)...        complete(12), fail(14)...         complete(22)...
list → [9,11] (next batch)        list → [28,30,32,34] ...          list → [33,35] ...
...                                ...                               ...
"0 pending — done!"               "0 pending — done!"               "0 pending — done!"
```

If two workers race for the same class, only one wins the checkout — the other gets an empty result and moves to the next available class. No extra coordination between sessions is needed as long as they all talk to the same Brimley daemon.

### 6.4 Resumability

If a worker session ends mid-run:

1. Open a new VS Code chat session.
2. Call `get_run_summary(run_id)` to see where things stand.
3. Any classes stuck in `checked_out` (stale) can be released: `release_class(class_id)`.
4. Launch a new `brimley-coverage-worker` or `brimley-coverage-worker-isolated` with the same `run_id` — it picks up where the previous session left off.
5. When all work is done, call `close_run(run_id)`.

### 6.5 Agent Definition Sketches

```yaml
---
name: brimley-coverage-seeder
description: "Seeds a local Brimley coverage run: creates the run, builds JaCoCo, and imports the report into the shared queue."
tools:
  [
    read,
    search,
    execute,
    todo,
    create_run,
    import_jacoco_report,
    get_run_summary,
    close_run,
  ]
---
```

```yaml
---
name: brimley-coverage-worker
description: "Autonomous local Brimley worker. Pulls from the shared queue, writes tests in the current workspace, and reports results."
tools:
  [
    read,
    search,
    execute,
    todo,
    list_uncovered_classes,
    checkout_classes,
    complete_class,
    fail_class,
    release_class,
    get_run_summary,
  ]
---
```

```yaml
---
name: brimley-coverage-worker-isolated
description: "Autonomous local Brimley worker for parallel runs. Uses a dedicated git worktree and branch for source isolation while coordinating through the shared Brimley MCP queue."
tools:
  [
    read,
    search,
    execute,
    todo,
    list_uncovered_classes,
    checkout_classes,
    complete_class,
    fail_class,
    release_class,
    get_run_summary,
    import_jacoco_report,
  ]
---
```

```yaml
---
name: brimley-jacoco-coverage-analyst
description: "Hidden read-only helper for Brimley workers that need local JaCoCo analysis or edge-case recommendations before writing tests."
tools: [read, search]
user-invocable: false
---
```

> **Note:** Whether VS Code custom agents can reference MCP tools by name in the `tools:` field depends on the current VS Code agent SDK behavior. If not, the tools are still available to any agent that has MCP tool access enabled.

> **Worktree note:** Agents can create git worktrees themselves if they have terminal execution, but the current VS Code chat session may not be able to relocate into the new worktree automatically. In that case, the agent should stop after worktree creation and print the exact `cd` command and exact rerun prompt for the developer.

---

## 7. Install and Startup Sequence

**Installing**

```shell
poetry install --no-root
```

**Running**

```shell
poetry run brimley repl --root . --watch --mcp
```

## 8. Tool Summary Table

| Tool                     | Type   | MCP  | Reads DB      | Writes DB       | Called By      |
| ------------------------ | ------ | ---- | ------------- | --------------- | -------------- |
| `create_run`             | SQL    | tool | —             | runs            | seeder         |
| `import_jacoco_report`   | Python | tool | (XML file)    | classes, events | seeder         |
| `list_uncovered_classes` | SQL    | tool | classes       | —               | worker         |
| `checkout_classes`       | Python | tool | classes       | classes, events | worker         |
| `complete_class`         | SQL    | tool | —             | classes         | worker         |
| `fail_class`             | SQL    | tool | —             | classes         | worker         |
| `release_class`          | SQL    | tool | —             | classes         | worker         |
| `get_run_summary`        | SQL    | tool | runs, classes | —               | seeder, worker |
| `close_run`              | SQL    | tool | —             | runs            | seeder         |

All worker tools are called through the shared `brimley-coverage-swarm` MCP server. Workers do not open the database directly, even when they run from separate git worktrees.

---

## 9. Limitations and Future Considerations

1. **Local only.** The Brimley daemon and SQLite database run on the developer's machine. This is not a distributed system.

2. **Single-writer safety.** SQLite handles concurrent reads well but serializes writes. The checkout tool uses transactions to prevent double-claims. WAL mode is enabled by `init_db.py` to support multiple concurrent workers.

3. **Agent tool access.** Whether VS Code custom agents (`.agent.md` files) can explicitly restrict which MCP tools are available depends on the VS Code agent SDK version. Currently, MCP tools are globally available to all agents with MCP access.

4. **Brimley maturity.** Brimley is pre-production. The MCP integration works for local development iteration. Expect API changes.

5. **Worker concurrency limits.** The design supports many parallel workers, but each one runs `mvn test` which can be resource-intensive. Practical concurrency depends on machine resources. Workers that fail due to resource contention should `release_class` and exit gracefully.

6. **Coverage re-import.** A worker can re-run `mvn test jacoco:report` and then call `import_jacoco_report` again to refresh `post_*` metrics. In worktree-based runs, the import tool should accept a report path from that worker's worktree rather than assuming the daemon's current directory. The tool should handle upsert semantics (update existing rows rather than duplicating).

7. **Session relocation.** A worker can create its own worktree, but a VS Code chat session may not be able to switch into that worktree automatically. The fallback is a precise handoff: print the worktree path, the `cd` command, and the rerun prompt for the developer.

8. **Stale checkout detection.** A future enhancement could add a `checkout_timeout` column and a `release_stale_checkouts` tool that automatically returns classes that have been checked out for too long.

9. **Multiple runs.** You can seed multiple runs with different scopes and run workers against each. Workers just need a `run_id` to know which queue to pull from.
