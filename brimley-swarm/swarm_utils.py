from __future__ import annotations

import json
from dataclasses import dataclass
from pathlib import Path
from typing import Iterator
from xml.etree import ElementTree

from brimley import BrimleyContext


@dataclass(frozen=True)
class CoverageCounters:
    missed: int = 0
    covered: int = 0


@dataclass(frozen=True)
class ClassCoverage:
    fqcn: str
    package_name: str
    source_file: str | None
    instruction: CoverageCounters
    branch: CoverageCounters
    line: CoverageCounters
    complexity: CoverageCounters
    instruction_coverage: float | None
    branch_coverage: float | None


def compute_coverage_ratio(missed: int, covered: int) -> float | None:
    total = missed + covered
    if total <= 0:
        return None
    return covered / total


def resolve_project_root(ctx: BrimleyContext) -> Path:
    configured_root = str(getattr(ctx.config, "project_root", ".") or ".")
    project_root = Path(configured_root)
    if project_root.is_absolute():
        return project_root
    return (Path.cwd() / project_root).resolve()


def resolve_project_path(ctx: BrimleyContext, relative_or_absolute: str) -> Path:
    candidate = Path(relative_or_absolute)
    if candidate.is_absolute():
        return candidate
    return (resolve_project_root(ctx) / candidate).resolve()


def fqcn_to_test_path(test_source_root: str, fqcn: str) -> Path:
    package_name, _, class_name = fqcn.rpartition(".")
    package_path = Path(*package_name.split(".")) if package_name else Path()
    outer_class_name = class_name.split("$", 1)[0]
    return Path(test_source_root) / package_path / f"{outer_class_name}Test.java"


def detect_existing_test(ctx: BrimleyContext, fqcn: str) -> tuple[bool, str | None]:
    project_root = resolve_project_root(ctx)
    test_source_root = str(getattr(ctx.config, "test_source_root", "src/test/java") or "src/test/java")
    test_file = fqcn_to_test_path(test_source_root, fqcn)
    absolute_test_file = project_root / test_file
    if absolute_test_file.exists():
        return True, test_file.as_posix()
    return False, None


def parse_jacoco_report(report_path: Path) -> Iterator[ClassCoverage]:
    tree = ElementTree.parse(report_path)
    root = tree.getroot()

    for package_element in root.findall("package"):
        raw_package_name = package_element.attrib.get("name", "")
        package_name = raw_package_name.replace("/", ".")

        for class_element in package_element.findall("class"):
            raw_class_name = class_element.attrib.get("name", "")
            if "/" in raw_class_name:
                fqcn = raw_class_name.replace("/", ".")
            elif package_name:
                fqcn = f"{package_name}.{raw_class_name}"
            else:
                fqcn = raw_class_name

            counters = {
                counter.attrib["type"]: CoverageCounters(
                    missed=int(counter.attrib.get("missed", 0)),
                    covered=int(counter.attrib.get("covered", 0)),
                )
                for counter in class_element.findall("counter")
            }

            source_filename = class_element.attrib.get("sourcefilename")
            source_file = None
            if source_filename:
                package_path = Path(*package_name.split(".")) if package_name else Path()
                source_file = (Path("src/main/java") / package_path / source_filename).as_posix()

            instruction = counters.get("INSTRUCTION", CoverageCounters())
            branch = counters.get("BRANCH", CoverageCounters())
            line = counters.get("LINE", CoverageCounters())
            complexity = counters.get("COMPLEXITY", CoverageCounters())

            yield ClassCoverage(
                fqcn=fqcn,
                package_name=package_name,
                source_file=source_file,
                instruction=instruction,
                branch=branch,
                line=line,
                complexity=complexity,
                instruction_coverage=compute_coverage_ratio(
                    instruction.missed,
                    instruction.covered,
                ),
                branch_coverage=compute_coverage_ratio(branch.missed, branch.covered),
            )


def encode_event_detail(detail: dict[str, object]) -> str:
    return json.dumps(detail, sort_keys=True)