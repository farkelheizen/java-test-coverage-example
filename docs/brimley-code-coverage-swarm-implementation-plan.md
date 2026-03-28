# Brimley Code Coverage Swarm Implementation Plan

> Status: Assembly plan for building the local-only Brimley coverage swarm described in `docs/brimley-backed-agentic-test-coverage-swarm-design.md`.

## 1. Goal

Assemble the full local Brimley code coverage swarm so that:

- one local Brimley daemon owns all queue and database access
- the MCP server exposed to VS Code is named `brimley-coverage-swarm`
- one seeder creates a run and imports JaCoCo data
- one or more workers consume the same shared queue concurrently
- isolated workers can operate in separate git worktrees without touching SQLite directly
- the system can be validated end to end on this repository

## 2. Current Starting Point

The repository already contains a partial Brimley scaffold:

- `brimley-swarm/brimley.yaml`
- `brimley-swarm/init_db.py`
- `brimley-swarm/README.md`
- a live SQLite database under `brimley-swarm/swarm.db`
- local Brimley agent definitions under `.github/agents/`
- design and launch docs under `docs/`

What is still missing is the full tool implementation and the tests that prove the swarm is safe to run locally.

## 3. Delivery Strategy

Build the swarm in phases. Each phase should end in a verifiable checkpoint before moving to the next phase.

Recommended rule:

- do not start the next phase until the current phase's exit tests pass

## 4. Phase Plan

### Phase 0: Inventory and Baseline

**Objective**

Confirm the current scaffold, decide file ownership, and establish a clean baseline for the Python Brimley workspace.

**Deliverables**

- confirmed directory layout for `brimley-swarm/`
- agreed file names for SQL and Python tools
- documented test approach for the Python side

**Steps**

1. Inspect the existing contents of `brimley-swarm/`.
2. Confirm whether SQL tools will live as `.sql` files or Python functions where transaction logic is required.
3. Decide where Python tests will live, such as `brimley-swarm/tests/`.
4. Decide whether `pyproject.toml` should add a test dependency group for `pytest`.
5. Confirm that the local-only rule is preserved: agents use MCP tools, not direct SQLite access.

**Tests and Exit Criteria**

- `poetry install --no-root` succeeds in `brimley-swarm/`
- the Brimley workspace starts without schema errors
- the ownership of each planned file is documented

**Suggested verification commands**

```shell
cd brimley-swarm
poetry install --no-root
poetry run brimley repl --root . --watch --mcp
```

### Phase 1: Harden the Brimley Project Skeleton

**Objective**

Bring the Brimley Python workspace to a state where it can support implementation and testing comfortably.

**Deliverables**

- updated `pyproject.toml` with test dependencies if needed
- test directory scaffold
- fixtures directory scaffold for sample JaCoCo data
- README notes describing how to run tests

**Steps**

1. Add a test dependency strategy, preferably `pytest`.
2. Create `brimley-swarm/tests/`.
3. Create `brimley-swarm/tests/fixtures/`.
4. Add one or more sample JaCoCo fixture files.
5. Update the Brimley README with test commands.

**Tests and Exit Criteria**

- the Python environment installs test dependencies successfully
- the test runner can execute an empty or initial smoke test suite
- fixture files are available for later import-tool tests

**Suggested verification commands**

```shell
cd brimley-swarm
poetry install --no-root
poetry run pytest
```

### Phase 2: Finalize Schema and Startup Behavior

**Objective**

Make sure schema initialization and database behavior fully match the design doc.

**Deliverables**

- verified `init_db.py`
- schema parity with the design doc
- WAL mode and indexes confirmed
- optional helper for schema reset in test environments only

**Steps**

1. Compare the actual `_SCHEMA_SQL` with the design doc schema.
2. Add any missing constraints or indexes.
3. Confirm startup behavior is idempotent.
4. Add a test-only reset helper if database cleanup is needed between tests.
5. Decide whether the shared DB path needs configuration hardening for worktree-based runs.

**Tests and Exit Criteria**

- starting the Brimley daemon twice does not corrupt or recreate data incorrectly
- schema inspection shows all planned tables and indexes exist
- WAL mode is enabled

**Suggested tests**

- unit test that `init_swarm_db` can run repeatedly
- integration test that verifies `runs`, `classes`, and `events` exist after startup
- integration test that verifies indexes exist

### Phase 3: Implement Read/Write MCP SQL Tools

**Objective**

Build the SQL-backed MCP tools that do not need custom Python logic.

**Deliverables**

- `create_run.sql`
- `list_uncovered_classes.sql`
- `complete_class.sql`
- `fail_class.sql`
- `release_class.sql`
- `get_run_summary.sql`
- `close_run.sql`

**Steps**

1. Create `create_run.sql`.
2. Create `list_uncovered_classes.sql`.
3. Create `complete_class.sql`.
4. Create `fail_class.sql`.
5. Create `release_class.sql`.
6. Create `get_run_summary.sql`.
7. Create `close_run.sql`.
8. Register and verify each tool through Brimley.

**Tests and Exit Criteria**

- each SQL tool loads successfully in Brimley
- each SQL tool returns the expected shape
- lifecycle transitions are correct for normal and invalid states

**Suggested tests**

- create a run and verify returned `run_id`
- query a seeded pending class list and verify ordering
- complete, fail, and release only work from valid prior states
- summary values reflect changed class states accurately

### Phase 4: Implement Python MCP Tools

**Objective**

Build the Python tools that need XML parsing, file-system inspection, or transactional updates.

**Deliverables**

- `import_jacoco_report.py`
- `checkout_classes.py`
- any shared helper module needed for path resolution or coverage calculations

**Steps**

1. Implement `import_jacoco_report.py`.
2. Parse JaCoCo XML into class-level metrics.
3. Detect whether a matching test file already exists.
4. Upsert `classes` rows for a run.
5. Emit `import` events.
6. Implement `checkout_classes.py` using atomic transactional updates.
7. Emit `checkout` events.
8. Confirm both tools behave correctly when scoped to a package prefix.

**Tests and Exit Criteria**

- the import tool correctly parses the sample JaCoCo fixture
- the import tool computes coverage ratios correctly
- the import tool respects `scope_filter`
- the checkout tool cannot double-claim the same class
- the checkout tool handles partial success correctly during races

**Suggested tests**

- unit tests for coverage-ratio calculation
- integration test for importing a known JaCoCo fixture into an empty DB
- integration test for re-import and upsert semantics
- concurrency-oriented test for two checkout attempts against the same rows

### Phase 5: Add Event Logging and Failure Semantics

**Objective**

Make the swarm resumable and debuggable through complete event coverage.

**Deliverables**

- event logging for all important state transitions
- consistent `agent_id` handling
- failure messaging conventions

**Steps**

1. Confirm every tool that changes state also records an event.
2. Standardize event types such as `import`, `checkout`, `complete`, `fail`, `release`, and `close_run`.
3. Decide the expected `detail` format for structured event payloads.
4. Standardize `agent_id` usage for local worker sessions.
5. Confirm failure messages are preserved and queryable.

**Tests and Exit Criteria**

- every write action produces a matching event row
- event payloads are sufficient to reconstruct the run history
- failed classes keep their error message intact

**Suggested tests**

- integration tests that assert event rows after every state mutation
- resumability test that reconstructs state from `runs`, `classes`, and `events`

### Phase 6: Support Worktree-Based Local Concurrency

**Objective**

Validate the local concurrency model where workers run from separate git worktrees but share one Brimley daemon and one queue.

**Deliverables**

- documented shared-daemon assumptions
- tested worktree path handling for worker-side JaCoCo imports
- proven local concurrency pattern for at least two workers

**Steps**

1. Confirm the daemon uses one shared database path.
2. Confirm workers never touch SQLite directly.
3. Confirm `import_jacoco_report` can accept a worker-specific report path.
4. Launch two isolated workers against the same `run_id` with disjoint scopes.
5. Confirm they can progress without queue collisions.
6. Confirm the worktree handoff flow is documented and practical.

**Tests and Exit Criteria**

- two workers can check out different classes from the same run
- no class is claimed twice
- post-test JaCoCo import works from a non-daemon worktree path
- the system remains stable when one worker exits mid-run

**Suggested tests**

- manual or scripted two-worker checkout smoke test
- integration test for stale checkout recovery
- import test using an explicit report path from a second worktree

### Phase 7: End-to-End Local Run

**Objective**

Prove that the swarm works from seeding through worker completion in this repository.

**Deliverables**

- one documented end-to-end local run
- one documented concurrent run with isolated workers
- final verification notes and known gaps

**Steps**

1. Start the Brimley daemon.
2. Seed a run for a narrow package.
3. Launch one worker and complete at least one class.
4. Seed another run or reuse the same run for a broader package.
5. Launch two isolated workers concurrently.
6. Verify the run summary changes as work completes.
7. Capture logs, outcomes, and any operational gaps.

**Tests and Exit Criteria**

- seeder creates a run successfully
- workers consume and complete queue entries successfully
- Maven still passes after generated tests are written
- run summary reflects completion and failure counts accurately

**Suggested verification commands**

```shell
cd brimley-swarm
poetry run brimley repl --root . --watch --mcp
```

```shell
cd ..
mvn clean test jacoco:report
```

### Phase 8: Documentation, Runbooks, and Cleanup

**Objective**

Leave the swarm maintainable for future local use.

**Deliverables**

- updated Brimley README
- completed launch guide
- implementation notes for future contributors
- explicit known-limitations section

**Steps**

1. Update `brimley-swarm/README.md` with install, run, and test commands.
2. Keep `docs/how-to-kick-off-the-autonomous-swarm.md` aligned with the final agent set.
3. Update the design doc if implementation decisions differ from the proposal.
4. Document how to inspect `swarm.db` safely for debugging.
5. Document common recovery actions such as releasing stale classes.

**Tests and Exit Criteria**

- documentation matches actual commands
- a new contributor can follow the docs to start the daemon and seed a run
- known limitations are explicit rather than implied

## 5. Test Matrix

The implementation should include tests at four levels.

### A. Unit Tests

Use for:

- coverage ratio calculations
- JaCoCo parsing helpers
- path conversion logic
- event payload formatting

### B. Integration Tests

Use for:

- schema initialization
- SQL tool behavior against a test SQLite database
- Python tool behavior against fixture data
- event logging and state transitions

### C. Concurrency Tests

Use for:

- simultaneous checkout attempts
- stale checkout handling
- multi-worker shared queue behavior

### D. End-to-End Smoke Tests

Use for:

- Brimley startup
- seeding a run
- worker completion flow
- Maven and JaCoCo validation against the Java project

## 6. Recommended File Assembly Order

Build files in this order to minimize blocked work:

1. `brimley-swarm/pyproject.toml`
2. `brimley-swarm/tests/` scaffold and fixtures
3. `brimley-swarm/init_db.py`
4. SQL tool files
5. `brimley-swarm/import_jacoco_report.py`
6. `brimley-swarm/checkout_classes.py`
7. shared helper modules, if needed
8. README and docs updates

## 7. Definition of Done

The Brimley coverage swarm is assembled when all of the following are true:

- the Brimley daemon starts locally without manual schema setup
- all planned MCP tools are implemented and discoverable
- direct SQLite access is not required by agents
- one seeder can populate a run from JaCoCo
- one worker can complete classes from that run
- two isolated workers can consume the same queue concurrently without double-claiming classes
- the Java repository still passes `mvn test`
- the docs accurately describe installation, startup, seeding, and worker execution

## 8. Suggested First Execution Slice

To reduce risk, build and validate this narrow slice first:

1. `init_db.py`
2. `create_run.sql`
3. `list_uncovered_classes.sql`
4. `import_jacoco_report.py`
5. `get_run_summary.sql`
6. one smoke test that seeds and summarizes a run

After that slice passes, add checkout and completion behavior.