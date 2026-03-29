from __future__ import annotations

from sqlalchemy import text

from brimley import BrimleyContext, function

from swarm_utils import detect_existing_test, encode_event_detail, parse_jacoco_report, resolve_project_path


@function(name="import_jacoco_report", mcpType="tool")
def import_jacoco_report(
    run_id: int,
    report_path: str = "target/site/jacoco/jacoco.xml",
    scope_filter: str = "",
    ctx: BrimleyContext | None = None,
) -> dict[str, object]:
    if ctx is None:
        raise ValueError("BrimleyContext is required")

    engine = ctx.databases["default"]
    resolved_report_path = resolve_project_path(ctx, report_path)
    if not resolved_report_path.exists():
        raise FileNotFoundError(f"JaCoCo report not found: {resolved_report_path}")

    imported_count = 0
    scope_prefix = scope_filter.strip()

    with engine.begin() as conn:
        run_exists = conn.execute(
            text("SELECT 1 FROM runs WHERE id = :run_id"),
            {"run_id": run_id},
        ).scalar_one_or_none()
        if run_exists is None:
            raise ValueError(f"Run {run_id} does not exist")

        for record in parse_jacoco_report(resolved_report_path):
            if scope_prefix and not record.fqcn.startswith(scope_prefix):
                continue

            has_existing_test, test_file = detect_existing_test(ctx, record.fqcn)
            class_id = conn.execute(
                text(
                    """
                    INSERT INTO classes (
                        run_id,
                        fqcn,
                        package_name,
                        source_file,
                        instruction_missed,
                        instruction_covered,
                        branch_missed,
                        branch_covered,
                        line_missed,
                        line_covered,
                        complexity_missed,
                        complexity_covered,
                        instruction_coverage,
                        branch_coverage,
                        has_existing_test,
                        test_file
                    )
                    VALUES (
                        :run_id,
                        :fqcn,
                        :package_name,
                        :source_file,
                        :instruction_missed,
                        :instruction_covered,
                        :branch_missed,
                        :branch_covered,
                        :line_missed,
                        :line_covered,
                        :complexity_missed,
                        :complexity_covered,
                        :instruction_coverage,
                        :branch_coverage,
                        :has_existing_test,
                        :test_file
                    )
                    ON CONFLICT(run_id, fqcn) DO UPDATE SET
                        package_name = excluded.package_name,
                        source_file = excluded.source_file,
                        instruction_missed = excluded.instruction_missed,
                        instruction_covered = excluded.instruction_covered,
                        branch_missed = excluded.branch_missed,
                        branch_covered = excluded.branch_covered,
                        line_missed = excluded.line_missed,
                        line_covered = excluded.line_covered,
                        complexity_missed = excluded.complexity_missed,
                        complexity_covered = excluded.complexity_covered,
                        instruction_coverage = excluded.instruction_coverage,
                        branch_coverage = excluded.branch_coverage,
                        has_existing_test = excluded.has_existing_test,
                        test_file = COALESCE(classes.test_file, excluded.test_file)
                    RETURNING id
                    """
                ),
                {
                    "run_id": run_id,
                    "fqcn": record.fqcn,
                    "package_name": record.package_name,
                    "source_file": record.source_file,
                    "instruction_missed": record.instruction.missed,
                    "instruction_covered": record.instruction.covered,
                    "branch_missed": record.branch.missed,
                    "branch_covered": record.branch.covered,
                    "line_missed": record.line.missed,
                    "line_covered": record.line.covered,
                    "complexity_missed": record.complexity.missed,
                    "complexity_covered": record.complexity.covered,
                    "instruction_coverage": record.instruction_coverage,
                    "branch_coverage": record.branch_coverage,
                    "has_existing_test": int(has_existing_test),
                    "test_file": test_file,
                },
            ).scalar_one()

            conn.execute(
                text(
                    """
                    INSERT INTO events (run_id, class_id, event_type, detail)
                    VALUES (:run_id, :class_id, 'import', :detail)
                    """
                ),
                {
                    "run_id": run_id,
                    "class_id": class_id,
                    "detail": encode_event_detail(
                        {
                            "fqcn": record.fqcn,
                            "has_existing_test": has_existing_test,
                            "report_path": str(resolved_report_path),
                        }
                    ),
                },
            )
            imported_count += 1

    return {
        "run_id": run_id,
        "report_path": str(resolved_report_path),
        "scope_filter": scope_prefix,
        "classes_imported": imported_count,
    }