
# Brimley Coverage Swarm

This directory hosts the local Brimley-backed coverage swarm for the Java project in the repository root.

The current implementation provides:

- seed-path tools for creating runs, importing JaCoCo XML, listing queue candidates, and summarizing progress
- queue lifecycle tools for checkout, complete, fail, release, and close-run operations
- pytest coverage for seed flow, lifecycle transitions, absolute worktree report paths, and double-claim prevention

## Install

```shell
poetry install --no-root
```

## Run The MCP Server

```shell
poetry run brimley repl --root . --watch --mcp
```

That serves the local MCP endpoint consumed by the `brimley-coverage-swarm` VS Code server entry.

## Validate The Project

Run the Brimley-side test suite:

```shell
poetry run pytest
```

Check Brimley discovery:

```shell
poetry run brimley build --root .
```

Rebuild the Java project and JaCoCo report from the repository root:

```shell
cd ..
mvn test
```

## Implemented Tools

SQL-backed tools:

- `create_run`
- `list_uncovered_classes`
- `get_run_summary`

Python-backed tools:

- `import_jacoco_report`
- `checkout_classes`
- `complete_class`
- `fail_class`
- `release_class`
- `close_run`

The state-changing tools are Python-backed so each mutation can write a matching event row in the same transaction.

## Real Smoke Run

With a JaCoCo report already present under the repository root, a minimal local smoke run is:

```shell
poetry run brimley repl --root . --watch --mcp
```

Then use the local agents documented in [docs/how-to-kick-off-the-autonomous-swarm.md](../docs/how-to-kick-off-the-autonomous-swarm.md).

## Worktree Notes

- The daemon owns one shared SQLite database.
- Workers should never write SQLite directly.
- Isolated workers may pass an absolute JaCoCo `report_path` from their own worktree.
- Re-import is upsert-based, so re-running coverage refreshes existing class rows instead of duplicating them.

## Known Limitations

- `brimley build --root .` generates `brimley_assets.py`; that file is ignored and should remain uncommitted.
- Automatic stale checkout expiry is not implemented; recovery currently uses `release_class`.
- End-to-end worker execution is still driven by the VS Code agents rather than a standalone CLI wrapper.
