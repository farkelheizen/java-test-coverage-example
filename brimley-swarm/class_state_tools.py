from __future__ import annotations

from brimley import BrimleyContext, function
from sqlalchemy import text

from swarm_utils import get_class_row, get_engine, insert_event


def _require_context(ctx: BrimleyContext | None) -> BrimleyContext:
    if ctx is None:
        raise ValueError("BrimleyContext is required")
    return ctx


@function(name="complete_class", mcpType="tool")
def complete_class(
    class_id: int,
    test_file: str,
    post_instruction_coverage: float | None = None,
    post_branch_coverage: float | None = None,
    agent_id: str | None = None,
    ctx: BrimleyContext | None = None,
) -> dict[str, object] | None:
    context = _require_context(ctx)
    engine = get_engine(context)

    with engine.begin() as conn:
        updated = conn.execute(
            text(
                """
                UPDATE classes
                SET status = 'completed',
                    completed_at = datetime('now'),
                    test_file = :test_file,
                    post_instruction_coverage = :post_instruction_coverage,
                    post_branch_coverage = :post_branch_coverage,
                    error_message = NULL
                WHERE id = :class_id
                  AND status = 'checked_out'
                RETURNING id, run_id, fqcn, status, test_file,
                          checked_out_by, post_instruction_coverage,
                          post_branch_coverage
                """
            ),
            {
                "class_id": class_id,
                "test_file": test_file,
                "post_instruction_coverage": post_instruction_coverage,
                "post_branch_coverage": post_branch_coverage,
            },
        ).mappings().one_or_none()

        if updated is None:
            return None

        row = dict(updated)
        resolved_agent_id = agent_id or row.get("checked_out_by")
        insert_event(
            conn,
            run_id=row["run_id"],
            class_id=row["id"],
            event_type="complete",
            agent_id=resolved_agent_id,
            detail={
                "fqcn": row["fqcn"],
                "test_file": test_file,
                "post_instruction_coverage": post_instruction_coverage,
                "post_branch_coverage": post_branch_coverage,
            },
        )
        return {
            "id": row["id"],
            "fqcn": row["fqcn"],
            "status": row["status"],
            "test_file": row["test_file"],
        }


@function(name="fail_class", mcpType="tool")
def fail_class(
    class_id: int,
    error_message: str,
    agent_id: str | None = None,
    ctx: BrimleyContext | None = None,
) -> dict[str, object] | None:
    context = _require_context(ctx)
    engine = get_engine(context)

    with engine.begin() as conn:
        updated = conn.execute(
            text(
                """
                UPDATE classes
                SET status = 'failed',
                    completed_at = datetime('now'),
                    error_message = :error_message
                WHERE id = :class_id
                  AND status = 'checked_out'
                RETURNING id, run_id, fqcn, status, error_message, checked_out_by
                """
            ),
            {
                "class_id": class_id,
                "error_message": error_message,
            },
        ).mappings().one_or_none()

        if updated is None:
            return None

        row = dict(updated)
        resolved_agent_id = agent_id or row.get("checked_out_by")
        insert_event(
            conn,
            run_id=row["run_id"],
            class_id=row["id"],
            event_type="fail",
            agent_id=resolved_agent_id,
            detail={
                "fqcn": row["fqcn"],
                "error_message": error_message,
            },
        )
        return {
            "id": row["id"],
            "fqcn": row["fqcn"],
            "status": row["status"],
            "error_message": row["error_message"],
        }


@function(name="release_class", mcpType="tool")
def release_class(
    class_id: int,
    agent_id: str | None = None,
    reason: str = "released",
    ctx: BrimleyContext | None = None,
) -> dict[str, object] | None:
    context = _require_context(ctx)
    engine = get_engine(context)

    with engine.begin() as conn:
        existing = get_class_row(conn, class_id)
        if existing is None or existing["status"] != "checked_out":
            return None

        conn.execute(
            text(
                """
                UPDATE classes
                SET status = 'pending',
                    checked_out_by = NULL,
                    checked_out_at = NULL
                WHERE id = :class_id
                """
            ),
            {"class_id": class_id},
        )

        resolved_agent_id = agent_id or existing.get("checked_out_by")
        insert_event(
            conn,
            run_id=existing["run_id"],
            class_id=existing["id"],
            event_type="release",
            agent_id=resolved_agent_id,
            detail={
                "fqcn": existing["fqcn"],
                "reason": reason,
            },
        )

        return {
            "id": existing["id"],
            "fqcn": existing["fqcn"],
            "status": "pending",
        }


@function(name="close_run", mcpType="tool")
def close_run(
    run_id: int,
    status: str = "completed",
    notes: str = "",
    agent_id: str | None = None,
    ctx: BrimleyContext | None = None,
) -> dict[str, object] | None:
    context = _require_context(ctx)
    if status not in {"completed", "aborted"}:
        raise ValueError("status must be 'completed' or 'aborted'")

    engine = get_engine(context)
    with engine.begin() as conn:
        updated = conn.execute(
            text(
                """
                UPDATE runs
                SET status = :status,
                    closed_at = datetime('now'),
                    notes = CASE
                        WHEN :notes = '' THEN notes
                        ELSE :notes
                    END
                WHERE id = :run_id
                  AND status = 'open'
                RETURNING id, name, status, closed_at, notes
                """
            ),
            {
                "run_id": run_id,
                "status": status,
                "notes": notes,
            },
        ).mappings().one_or_none()

        if updated is None:
            return None

        row = dict(updated)
        insert_event(
            conn,
            run_id=row["id"],
            event_type="close_run",
            agent_id=agent_id,
            detail={
                "status": row["status"],
                "notes": row["notes"] or "",
            },
        )
        return {
            "id": row["id"],
            "name": row["name"],
            "status": row["status"],
            "closed_at": row["closed_at"],
        }