/*
---
name: get_run_summary
type: sql_function
description: Returns aggregate progress and coverage metrics for a run.
connection: default
arguments:
  inline:
    run_id:
      type: int
return_shape:
  inline:
    run_id: int
    name: string
    run_status: string
    class_limit: int
    coverage_target: float
    total_classes: int
    pending: int
    checked_out: int
    completed: int
    failed: int
    skipped: int
    avg_instruction_coverage: float
    avg_branch_coverage: float
    avg_post_instruction_coverage: float
    avg_post_branch_coverage: float
mcp:
  type: tool
---
*/
SELECT
    r.id AS run_id,
    r.name,
    r.status AS run_status,
    r.class_limit,
    COALESCE(r.coverage_target, 0.0) AS coverage_target,
    COUNT(c.id) AS total_classes,
    COALESCE(SUM(CASE WHEN c.status = 'pending' THEN 1 ELSE 0 END), 0) AS pending,
    COALESCE(SUM(CASE WHEN c.status = 'checked_out' THEN 1 ELSE 0 END), 0) AS checked_out,
    COALESCE(SUM(CASE WHEN c.status = 'completed' THEN 1 ELSE 0 END), 0) AS completed,
    COALESCE(SUM(CASE WHEN c.status = 'failed' THEN 1 ELSE 0 END), 0) AS failed,
    COALESCE(SUM(CASE WHEN c.status = 'skipped' THEN 1 ELSE 0 END), 0) AS skipped,
    COALESCE(ROUND(AVG(c.instruction_coverage), 4), 0.0) AS avg_instruction_coverage,
    COALESCE(ROUND(AVG(c.branch_coverage), 4), 0.0) AS avg_branch_coverage,
    COALESCE(ROUND(AVG(c.post_instruction_coverage), 4), 0.0) AS avg_post_instruction_coverage,
    COALESCE(ROUND(AVG(c.post_branch_coverage), 4), 0.0) AS avg_post_branch_coverage
FROM runs r
LEFT JOIN classes c ON c.run_id = r.id
WHERE r.id = :run_id
GROUP BY r.id, r.name, r.status, r.class_limit, r.coverage_target;