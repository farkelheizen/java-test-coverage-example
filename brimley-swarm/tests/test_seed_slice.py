from __future__ import annotations

import shutil
import sys
from pathlib import Path

from sqlalchemy import create_engine, text

BRIMLEY_ROOT = Path(__file__).resolve().parents[1]
if str(BRIMLEY_ROOT) not in sys.path:
    sys.path.insert(0, str(BRIMLEY_ROOT))

from brimley.core.context import BrimleyContext
from brimley.discovery.sql_parser import parse_sql_file
from brimley.execution.sql_runner import SqlRunner

from import_jacoco_report import import_jacoco_report
from init_db import init_swarm_db
from swarm_utils import compute_coverage_ratio


FIXTURES_DIR = Path(__file__).parent / "fixtures"


def build_context(database_path: Path, project_root: Path) -> BrimleyContext:
    engine = create_engine(f"sqlite:///{database_path}")
    return BrimleyContext(
        config_dict={
            "config": {
                "project_root": str(project_root),
                "test_source_root": "src/test/java",
                "main_source_root": "src/main/java",
            }
        },
        databases={"default": engine},
    )


def run_sql_tool(name: str, ctx: BrimleyContext, args: dict[str, object]) -> object:
    sql_function = parse_sql_file(BRIMLEY_ROOT / f"{name}.sql")
    return SqlRunner().run(sql_function, args, ctx)


def test_seed_slice_imports_and_summarizes_run(tmp_path: Path) -> None:
    project_root = tmp_path / "java-project"
    database_path = tmp_path / "swarm.db"
    report_dir = project_root / "target/site/jacoco"
    test_file = project_root / "src/test/java/com/example/service/MathServiceTest.java"

    report_dir.mkdir(parents=True)
    test_file.parent.mkdir(parents=True)
    test_file.write_text("class MathServiceTest {}\n", encoding="utf-8")
    shutil.copyfile(FIXTURES_DIR / "jacoco_sample.xml", report_dir / "jacoco.xml")

    ctx = build_context(database_path=database_path, project_root=project_root)
    init_swarm_db(ctx)

    run = run_sql_tool(
        "create_run",
        ctx,
        {
            "name": "service-seed",
            "target_scope": "com.example.service",
            "class_limit": 5,
            "coverage_target": 0.7,
            "notes": "seed smoke test",
        },
    )
    assert run["status"] == "open"

    result = import_jacoco_report(
        run_id=run["run_id"],
        scope_filter="com.example.service",
        ctx=ctx,
    )
    assert result == {
        "run_id": run["run_id"],
        "report_path": str((report_dir / "jacoco.xml").resolve()),
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

    assert imported_events == 2
    assert math_service["has_existing_test"] == 1
    assert math_service["test_file"] == "src/test/java/com/example/service/MathServiceTest.java"


def test_compute_coverage_ratio_handles_zero_total() -> None:
    assert compute_coverage_ratio(0, 0) is None
    assert compute_coverage_ratio(3, 1) == 0.25