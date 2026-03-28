"""Swarm database initialization via Brimley @on_startup lifecycle hook.

On daemon boot, this hook ensures the swarm schema (runs, classes, events)
exists in the configured SQLite database.  It is safe to run repeatedly —
every statement uses IF NOT EXISTS.
"""
from __future__ import annotations

from loguru import logger

from brimley import BrimleyContext, on_startup


_SCHEMA_SQL = """
CREATE TABLE IF NOT EXISTS runs (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    name            TEXT NOT NULL,
    target_scope    TEXT NOT NULL,
    class_limit     INTEGER NOT NULL DEFAULT 5,
    coverage_target REAL,
    status          TEXT NOT NULL DEFAULT 'open'
                        CHECK (status IN ('open', 'completed', 'aborted')),
    created_at      TEXT NOT NULL DEFAULT (datetime('now')),
    closed_at       TEXT,
    notes           TEXT
);

CREATE TABLE IF NOT EXISTS classes (
    id                      INTEGER PRIMARY KEY AUTOINCREMENT,
    run_id                  INTEGER NOT NULL REFERENCES runs(id),
    fqcn                    TEXT NOT NULL,
    package_name            TEXT NOT NULL,
    source_file             TEXT,
    instruction_missed      INTEGER NOT NULL DEFAULT 0,
    instruction_covered     INTEGER NOT NULL DEFAULT 0,
    branch_missed           INTEGER NOT NULL DEFAULT 0,
    branch_covered          INTEGER NOT NULL DEFAULT 0,
    line_missed             INTEGER NOT NULL DEFAULT 0,
    line_covered            INTEGER NOT NULL DEFAULT 0,
    complexity_missed       INTEGER NOT NULL DEFAULT 0,
    complexity_covered      INTEGER NOT NULL DEFAULT 0,
    instruction_coverage    REAL,
    branch_coverage         REAL,
    has_existing_test       INTEGER NOT NULL DEFAULT 0,
    status                  TEXT NOT NULL DEFAULT 'pending'
                                CHECK (status IN (
                                    'pending',
                                    'checked_out',
                                    'completed',
                                    'failed',
                                    'skipped'
                                )),
    checked_out_by          TEXT,
    checked_out_at          TEXT,
    completed_at            TEXT,
    post_instruction_coverage REAL,
    post_branch_coverage      REAL,
    test_file               TEXT,
    error_message           TEXT,
    UNIQUE(run_id, fqcn)
);

CREATE TABLE IF NOT EXISTS events (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    run_id      INTEGER NOT NULL REFERENCES runs(id),
    class_id    INTEGER REFERENCES classes(id),
    event_type  TEXT NOT NULL,
    agent_id    TEXT,
    detail      TEXT,
    created_at  TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE INDEX IF NOT EXISTS idx_classes_run_status ON classes(run_id, status);
CREATE INDEX IF NOT EXISTS idx_classes_run_fqcn   ON classes(run_id, fqcn);
CREATE INDEX IF NOT EXISTS idx_events_run         ON events(run_id);
CREATE INDEX IF NOT EXISTS idx_events_class       ON events(class_id);

PRAGMA journal_mode = WAL;
"""


@on_startup
def init_swarm_db(ctx: BrimleyContext) -> None:
    """Creates the swarm schema tables if they do not already exist."""
    logger.info("Initializing swarm database schema...")
    db = ctx.databases["default"]
    with db.connect() as conn:
        conn.connection.executescript(_SCHEMA_SQL)
    logger.info("Swarm database schema initialized (WAL mode enabled)")
