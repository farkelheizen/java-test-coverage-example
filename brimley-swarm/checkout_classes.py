from __future__ import annotations

from sqlalchemy import bindparam, text

from brimley import BrimleyContext, function

from swarm_utils import get_engine, insert_event


@function(name="checkout_classes", mcpType="tool")
def checkout_classes(
    run_id: int,
    class_ids: list[int],
    agent_id: str,
    ctx: BrimleyContext | None = None,
) -> dict[str, object]:
    if ctx is None:
        raise ValueError("BrimleyContext is required")

    if not class_ids:
        return {"checked_out": [], "count": 0}

    engine = get_engine(ctx)
    unique_ids = list(dict.fromkeys(class_ids))
    update_stmt = text(
        """
        UPDATE classes
        SET status = 'checked_out',
            checked_out_by = :agent_id,
            checked_out_at = datetime('now')
        WHERE run_id = :run_id
          AND id IN :class_ids
          AND status = 'pending'
        RETURNING id, run_id, fqcn, package_name, checked_out_by, checked_out_at
        """
    ).bindparams(bindparam("class_ids", expanding=True))

    with engine.begin() as conn:
        claimed_rows = [
            dict(row)
            for row in conn.execute(
                update_stmt,
                {
                    "run_id": run_id,
                    "class_ids": unique_ids,
                    "agent_id": agent_id,
                },
            ).mappings()
        ]

        for row in claimed_rows:
            insert_event(
                conn,
                run_id=row["run_id"],
                class_id=row["id"],
                event_type="checkout",
                agent_id=agent_id,
                detail={
                    "fqcn": row["fqcn"],
                    "package_name": row["package_name"],
                },
            )

    return {
        "checked_out": [row["fqcn"] for row in claimed_rows],
        "count": len(claimed_rows),
    }