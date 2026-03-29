from __future__ import annotations

import threading
from pathlib import Path

from sqlalchemy import text

from checkout_classes import checkout_classes
from class_state_tools import close_run, complete_class, fail_class, release_class
from conftest import build_context, seed_run
from import_jacoco_report import import_jacoco_report


def test_lifecycle_tools_emit_events_and_update_summary(tmp_path: Path) -> None:
    ctx, run, project_root, _ = seed_run(tmp_path, scope_filter="com.example")

    engine = ctx.databases["default"]
    with engine.connect() as conn:
        classes = list(
            conn.execute(
                text(
                    """
                    SELECT id, fqcn
                    FROM classes
                    WHERE run_id = :run_id
                    ORDER BY fqcn ASC
                    """
                ),
                {"run_id": run["run_id"]},
            ).mappings()
        )

    class_ids = [row["id"] for row in classes]
    checkout = checkout_classes(run["run_id"], class_ids, "worker-a", ctx=ctx)
    assert checkout["count"] == 3

    completed = complete_class(
        class_ids[0],
        "src/test/java/com/example/service/InsurancePremiumCalculatorTest.java",
        post_instruction_coverage=0.9,
        post_branch_coverage=0.5,
        ctx=ctx,
    )
    failed = fail_class(class_ids[1], "generation failed", ctx=ctx)
    released = release_class(class_ids[2], reason="handoff", ctx=ctx)
    rechecked = checkout_classes(run["run_id"], [class_ids[2]], "worker-b", ctx=ctx)
    completed_released = complete_class(
        class_ids[2],
        "src/test/java/com/example/util/JsonUtilsTest.java",
        post_instruction_coverage=0.8,
        post_branch_coverage=0.75,
        agent_id="worker-b",
        ctx=ctx,
    )
    closed = close_run(run["run_id"], notes="finished in test", agent_id="worker-b", ctx=ctx)

    assert completed["status"] == "completed"
    assert failed["status"] == "failed"
    assert released["status"] == "pending"
    assert rechecked == {"checked_out": ["com.example.util.JsonUtils"], "count": 1}
    assert completed_released["status"] == "completed"
    assert closed["status"] == "completed"

    with engine.connect() as conn:
        summary = conn.execute(
            text(
                """
                SELECT status, closed_at, notes
                FROM runs
                WHERE id = :run_id
                """
            ),
            {"run_id": run["run_id"]},
        ).mappings().one()
        class_rows = list(
            conn.execute(
                text(
                    """
                    SELECT fqcn, status, error_message
                    FROM classes
                    WHERE run_id = :run_id
                    ORDER BY fqcn ASC
                    """
                ),
                {"run_id": run["run_id"]},
            ).mappings()
        )
        event_rows = list(
            conn.execute(
                text(
                    """
                    SELECT event_type, agent_id
                    FROM events
                    WHERE run_id = :run_id
                    ORDER BY id ASC
                    """
                ),
                {"run_id": run["run_id"]},
            ).mappings()
        )

    assert summary["status"] == "completed"
    assert summary["closed_at"] is not None
    assert summary["notes"] == "finished in test"
    assert [row["status"] for row in class_rows] == ["completed", "failed", "completed"]
    assert class_rows[1]["error_message"] == "generation failed"
    assert [row["event_type"] for row in event_rows] == [
        "import",
        "import",
        "import",
        "checkout",
        "checkout",
        "checkout",
        "complete",
        "fail",
        "release",
        "checkout",
        "complete",
        "close_run",
    ]
    assert event_rows[3]["agent_id"] == "worker-a"
    assert event_rows[9]["agent_id"] == "worker-b"
    assert event_rows[-1]["agent_id"] == "worker-b"


def test_checkout_classes_prevents_double_claim_under_race(tmp_path: Path) -> None:
    ctx, run, project_root, _ = seed_run(tmp_path)
    engine = ctx.databases["default"]
    database_path = Path(str(engine.url.database))

    with engine.connect() as conn:
        class_id = conn.execute(
            text(
                """
                SELECT id
                FROM classes
                WHERE run_id = :run_id
                ORDER BY id ASC
                LIMIT 1
                """
            ),
            {"run_id": run["run_id"]},
        ).scalar_one()

    results: list[dict[str, object]] = []
    lock = threading.Lock()
    barrier = threading.Barrier(2)

    def worker(agent_id: str) -> None:
        local_ctx = build_context(database_path=database_path, project_root=project_root)
        barrier.wait()
        result = checkout_classes(run["run_id"], [class_id], agent_id, ctx=local_ctx)
        with lock:
            results.append(result)

    threads = [
        threading.Thread(target=worker, args=("worker-a",)),
        threading.Thread(target=worker, args=("worker-b",)),
    ]
    for thread in threads:
        thread.start()
    for thread in threads:
        thread.join()

    assert sorted(result["count"] for result in results) == [0, 1]
    claimed = [result["checked_out"] for result in results if result["count"] == 1]
    assert claimed == [["com.example.service.InsurancePremiumCalculator"]]


def test_import_accepts_absolute_report_path_and_upserts(tmp_path: Path) -> None:
    ctx, run, _, _ = seed_run(tmp_path, scope_filter="com.example.service", create_math_test=False)
    external_report = tmp_path / "other-worktree/target/site/jacoco/jacoco.xml"
    external_report.parent.mkdir(parents=True)
    external_report.write_text(
        """<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<report name=\"worktree\">
  <package name=\"com/example/service\">
    <class name=\"com/example/service/InsurancePremiumCalculator\" sourcefilename=\"InsurancePremiumCalculator.java\">
      <counter type=\"INSTRUCTION\" missed=\"2\" covered=\"18\"/>
      <counter type=\"BRANCH\" missed=\"1\" covered=\"3\"/>
      <counter type=\"LINE\" missed=\"1\" covered=\"9\"/>
      <counter type=\"COMPLEXITY\" missed=\"0\" covered=\"3\"/>
    </class>
    <class name=\"com/example/service/MathService\" sourcefilename=\"MathService.java\">
      <counter type=\"INSTRUCTION\" missed=\"0\" covered=\"40\"/>
      <counter type=\"BRANCH\" missed=\"0\" covered=\"8\"/>
      <counter type=\"LINE\" missed=\"0\" covered=\"15\"/>
      <counter type=\"COMPLEXITY\" missed=\"0\" covered=\"4\"/>
    </class>
  </package>
</report>
""",
        encoding="utf-8",
    )

    result = import_jacoco_report(
        run_id=run["run_id"],
        report_path=str(external_report),
        scope_filter="com.example.service",
        ctx=ctx,
    )
    assert result["classes_imported"] == 2
    assert result["report_path"] == str(external_report)

    engine = ctx.databases["default"]
    with engine.connect() as conn:
        counts = conn.execute(
            text(
                """
                SELECT COUNT(*) AS total_rows,
                       COUNT(DISTINCT fqcn) AS distinct_rows
                FROM classes
                WHERE run_id = :run_id
                """
            ),
            {"run_id": run["run_id"]},
        ).mappings().one()
        insurance = conn.execute(
            text(
                """
                SELECT instruction_missed, instruction_covered, instruction_coverage
                FROM classes
                WHERE run_id = :run_id AND fqcn = :fqcn
                """
            ),
            {
                "run_id": run["run_id"],
                "fqcn": "com.example.service.InsurancePremiumCalculator",
            },
        ).mappings().one()

    assert counts["total_rows"] == counts["distinct_rows"] == 2
    assert insurance["instruction_missed"] == 2
    assert insurance["instruction_covered"] == 18
    assert insurance["instruction_coverage"] == 0.9