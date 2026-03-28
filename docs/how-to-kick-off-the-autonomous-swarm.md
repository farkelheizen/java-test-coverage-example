## How to Kick Off the Local Brimley Swarm

The local-only Brimley agents live under `.github/agents/`.

- `brimley-coverage-seeder.agent.md` is the user-invocable entry point that creates a run, generates JaCoCo, and imports the shared queue.
- `brimley-coverage-worker.agent.md` is the user-invocable shared-workspace worker.
- `brimley-coverage-worker-isolated.agent.md` is the user-invocable worktree-first worker for concurrent local execution.
- `brimley-jacoco-coverage-analyst.agent.md` is a hidden, read-only helper for prioritization and edge-case analysis.

These agents are the local Brimley workflow. The older `coverage-*`, `jacoco-*`, and `java-test-writer` agents remain in the repository for reference, but this guide assumes you are using the `brimley-*` set.

See also:

- [Design Overview](./brimley-backed-agentic-test-coverage-swarm-design.md)
- [Implementation Plan](./brimley-code-coverage-swarm-implementation-plan.md)

## Before You Start

Decide these inputs first:

- target package or classes
- class limit `n`
- coverage target or stopping condition
- whether you want to work in the current workspace or a dedicated worktree
- worktree path and branch name if you want parallel isolated workers

Make sure the local `brimley-coverage-swarm` MCP server is running before invoking workers.

Example local startup:

```shell
poetry install --no-root
poetry run brimley repl --root . --watch --mcp
```

That exposes the shared MCP endpoint that all local Brimley agents use. In `.vscode/mcp.json`, the server name is `brimley-coverage-swarm`.

## Quick Start

Use this sequence for a normal local run:

1. Start the `brimley-coverage-swarm` MCP server.
2. Invoke `brimley-coverage-seeder` once to create a run and import JaCoCo coverage.
3. Launch one or more `brimley-coverage-worker` or `brimley-coverage-worker-isolated` sessions against that `run_id`.
4. Keep workers bounded to small batches.
5. When the queue is empty or the coverage target is met, stop.

## Seeding a Run

Use `brimley-coverage-seeder` to populate the shared queue without writing tests.

Example:

```text
/agent brimley-coverage-seeder Seed a Brimley run for com.example.service, limit to 12 classes, target 70% coverage.
```

Broader example:

```text
/agent brimley-coverage-seeder Seed a Brimley run for com.example.service and com.example.util, limit to 24 classes, target 75% coverage.
```

What the seeder does:

1. Creates a new run in the shared Brimley queue.
2. Runs `mvn clean test jacoco:report` when practical.
3. Imports JaCoCo data into the queue.
4. Returns a `run_id` and summary.

## Working in the Current Workspace

Use `brimley-coverage-worker` when you want one worker to write tests directly in the current workspace.

Example:

```text
/agent brimley-coverage-worker Work run 3, claim up to 3 classes from com.example.service, and stop after one batch if Maven fails.
```

Target specific classes:

```text
/agent brimley-coverage-worker Work run 3, claim up to 3 classes from com.example.service.OrderService, com.example.service.PaymentService, and com.example.service.InventoryService.
```

Use this mode when:

- you only want one active writer
- you do not need file-system isolation
- you are comfortable editing directly in the current workspace

## Running Concurrent Isolated Workers

Use `brimley-coverage-worker-isolated` when you want multiple local workers running at the same time.

Each worker should get:

- the same shared `run_id`
- a different git worktree path
- a different branch name
- a bounded class count
- a package or class filter when practical

Example worker A:

```text
/agent brimley-coverage-worker-isolated For run 3, create or reuse a worktree at ../brimley-service-a on branch worker/run-3-a, claim up to 3 classes from com.example.service.
```

Example worker B:

```text
/agent brimley-coverage-worker-isolated For run 3, create or reuse a worktree at ../brimley-util-b on branch worker/run-3-b, claim up to 3 classes from com.example.util.
```

Example worker C:

```text
/agent brimley-coverage-worker-isolated For run 3, create or reuse a worktree at ../brimley-mixed-c on branch worker/run-3-c, claim up to 2 classes from com.example.model.
```

Use this mode when:

- you want local concurrency
- you want separate uncommitted changes per worker
- you want to minimize file collisions between workers

## Recommended Launch Pattern

This is the simplest local sequence for parallel work:

1. Start Brimley.
2. Seed one run.
3. Launch two or three isolated workers with disjoint scopes.

Example sequence:

```text
/agent brimley-coverage-seeder Seed a Brimley run for com.example.service and com.example.util, limit to 12 classes, target 70% coverage.
```

After the seeder returns `run_id=5`:

```text
/agent brimley-coverage-worker-isolated For run 5, create or reuse a worktree at ../brimley-service-a on branch worker/run-5-a, claim up to 3 classes from com.example.service.
```

```text
/agent brimley-coverage-worker-isolated For run 5, create or reuse a worktree at ../brimley-service-b on branch worker/run-5-b, claim up to 3 classes from com.example.service, prioritize classes with no matching tests.
```

```text
/agent brimley-coverage-worker-isolated For run 5, create or reuse a worktree at ../brimley-util-a on branch worker/run-5-c, claim up to 3 classes from com.example.util.
```

## What Happens Next

`brimley-coverage-worker` and `brimley-coverage-worker-isolated` follow the same queue flow:

1. Read the current run summary.
2. List candidate classes from the shared queue.
3. Optionally consult `brimley-jacoco-coverage-analyst` for prioritization.
4. Check out a bounded batch.
5. Write or update tests.
6. Run Maven validation.
7. Mark classes completed, failed, or released through Brimley.

The isolated worker does the same work from inside a dedicated git worktree.

## Worktree Handoff Behavior

An isolated worker can create its own worktree, but the current VS Code chat session may not be able to relocate into that worktree automatically.

When that happens, the worker should stop and return a handoff like this:

```text
Worktree created: <path>
Next command: cd <path>
Next chat prompt: /agent brimley-coverage-worker-isolated <rerun prompt>
```

Run the `cd` command first, then rerun the returned `/agent ...` prompt from that worktree.

## Prompt Templates

Seeder template:

```text
/agent brimley-coverage-seeder Seed a Brimley run for <package(s) or class(es)>, limit to <n> classes, target <coverage target> coverage.
```

Shared-workspace worker template:

```text
/agent brimley-coverage-worker Work run <run_id>, claim up to <n> classes from <package(s) or class(es)>, and stop after one batch if Maven fails.
```

Isolated worker template:

```text
/agent brimley-coverage-worker-isolated For run <run_id>, create or reuse a worktree at <path> on branch <branch>, claim up to <n> classes from <package(s) or class(es)>.
```

## Troubleshooting

- Worker cannot see the queue: make sure the `brimley-coverage-swarm` MCP server is running and all sessions are pointed at the same local endpoint.
- Two workers touched the same files: use separate worktrees and keep scopes disjoint where practical.
- Worktree creation succeeded but execution stopped: use the returned handoff block exactly as written.
- A worker leaves classes checked out: start a new worker on the same `run_id` and release or finish the stale classes.
- Coverage import looks stale: make sure the worker imports the JaCoCo report path from its own worktree when using isolated mode.
