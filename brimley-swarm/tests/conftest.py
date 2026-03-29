from __future__ import annotations

import shutil
import sys
from pathlib import Path

import pytest
from sqlalchemy import create_engine

BRIMLEY_ROOT = Path(__file__).resolve().parents[1]
if str(BRIMLEY_ROOT) not in sys.path:
    sys.path.insert(0, str(BRIMLEY_ROOT))

from brimley.core.context import BrimleyContext
from brimley.discovery.sql_parser import parse_sql_file
from brimley.execution.sql_runner import SqlRunner

from import_jacoco_report import import_jacoco_report
from init_db import init_swarm_db


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


def create_project_layout(
    tmp_path: Path,
    *,
    report_fixture_name: str = "jacoco_sample.xml",
    create_math_test: bool = True,
) -> tuple[Path, Path, Path]:
    project_root = tmp_path / "java-project"
    database_path = tmp_path / "swarm.db"
    report_path = project_root / "target/site/jacoco/jacoco.xml"
    report_path.parent.mkdir(parents=True)
    shutil.copyfile(FIXTURES_DIR / report_fixture_name, report_path)

    if create_math_test:
        test_file = project_root / "src/test/java/com/example/service/MathServiceTest.java"
        test_file.parent.mkdir(parents=True)
        test_file.write_text("class MathServiceTest {}\n", encoding="utf-8")

    return project_root, database_path, report_path


def seed_run(
    tmp_path: Path,
    *,
    scope_filter: str = "com.example.service",
    report_path: str | None = None,
    create_math_test: bool = True,
) -> tuple[BrimleyContext, dict[str, object], Path, Path]:
    project_root, database_path, default_report_path = create_project_layout(
        tmp_path,
        create_math_test=create_math_test,
    )
    ctx = build_context(database_path=database_path, project_root=project_root)
    init_swarm_db(ctx)
    run = run_sql_tool(
        "create_run",
        ctx,
        {
            "name": "service-seed",
            "target_scope": scope_filter,
            "class_limit": 10,
            "coverage_target": 0.7,
            "notes": "test seed",
        },
    )
    import_jacoco_report(
        run_id=run["run_id"],
        scope_filter=scope_filter,
        report_path=report_path or str(default_report_path),
        ctx=ctx,
    )
    return ctx, run, project_root, default_report_path


@pytest.fixture
def seeded_service_run(tmp_path: Path) -> tuple[BrimleyContext, dict[str, object], Path, Path]:
    return seed_run(tmp_path)