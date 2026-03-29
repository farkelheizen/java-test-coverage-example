/*
---
name: list_uncovered_classes
type: sql_function
description: Lists pending classes ordered by lowest instruction coverage first.
connection: default
arguments:
  inline:
    run_id:
      type: int
    limit:
      type: int
      default: 10
    min_missed_instructions:
      type: int
      default: 0
return_shape: dict[]
mcp:
  type: tool
---
*/
SELECT
    id,
    fqcn,
    package_name,
    instruction_missed,
    instruction_covered,
    branch_missed,
    branch_covered,
    instruction_coverage,
    branch_coverage,
    has_existing_test,
    status
FROM classes
WHERE run_id = :run_id
  AND status = 'pending'
  AND instruction_missed >= :min_missed_instructions
ORDER BY instruction_coverage ASC, instruction_missed DESC, fqcn ASC
LIMIT :limit;