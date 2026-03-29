from __future__ import annotations

from pathlib import Path

from sqlalchemy import text

from import_jacoco_report import import_jacoco_report
from swarm_utils import compute_coverage_ratio

from conftest import run_sql_tool, seed_run


def test_seed_slice_imports_and_summarizes_run(tmp_path: Path) -> None:
    ctx, run, _, report_path = seed_run(tmp_path)
    assert run["status"] == "open"

    result = import_jacoco_report(
        run_id=run["run_id"],
        scope_filter="com.example.service",
        report_path=str(report_path),
        ctx=ctx,
    )
    assert result == {
        "run_id": run["run_id"],
        "report_path": str(report_path.resolve()),
        "scope_filter": "com.example.service",
        "classes_imported": 2,
    }

    queued = run_sql_tool(
        "list_uncovered_classes",
        ctx,
        {
            "run_id": run["run_id"],
            "limit": 10,
            "min_missed_instructions": 0,
        },
    )
    assert [row["fqcn"] for row in queued] == [
        "com.example.service.InsurancePremiumCalculator",
        "com.example.service.MathService",
    ]
    assert queued[0]["instruction_coverage"] == 0.0
    assert queued[1]["has_existing_test"] == 1

    summary = run_sql_tool("get_run_summary", ctx, {"run_id": run["run_id"]})
    assert summary["run_id"] == run["run_id"]
    assert summary["run_status"] == "open"
    assert summary["total_classes"] == 2
    assert summary["pending"] == 2
    assert summary["checked_out"] == 0
    assert summary["completed"] == 0
    assert summary["failed"] == 0
    assert summary["skipped"] == 0
    assert summary["avg_instruction_coverage"] == 0.375
    assert summary["avg_branch_coverage"] == 0.375

    engine = ctx.databases["default"]
    with engine.connect() as conn:
        imported_events = conn.execute(
            text("SELECT COUNT(*) FROM events WHERE run_id = :run_id AND event_type = 'import'"),
            {"run_id": run["run_id"]},
        ).scalar_one()
        total_classes = conn.execute(
            text("SELECT COUNT(*) FROM classes WHERE run_id = :run_id"),
            {"run_id": run["run_id"]},
        ).scalar_one()
        math_service = conn.execute(
            text(
                """
                SELECT has_existing_test, test_file
                FROM classes
                WHERE run_id = :run_id AND fqcn = :fqcn
                """
            ),
            {
                "run_id": run["run_id"],
                "fqcn": "com.example.service.MathService",
            },
        ).mappings().one()

    assert imported_events == 4
    assert total_classes == 2
    assert math_service["has_existing_test"] == 1
    assert math_service["test_file"] == "src/test/java/com/example/service/MathServiceTest.java"


def test_compute_coverage_ratio_handles_zero_total() -> None:
    assert compute_coverage_ratio(0, 0) is None
    assert compute_coverage_ratio(3, 1) == 0.25