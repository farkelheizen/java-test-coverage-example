/*
---
name: create_run
type: sql_function
description: Creates a new coverage swarm run.
connection: default
arguments:
  inline:
    name:
      type: str
    target_scope:
      type: str
    class_limit:
      type: int
      default: 5
    coverage_target:
      type: float
      default: 0.7
    notes:
      type: str
      default: null
return_shape:
  inline:
    run_id: int
    name: string
    status: string
    target_scope: string
    class_limit: int
    coverage_target: float
mcp:
  type: tool
---
*/
INSERT INTO runs (name, target_scope, class_limit, coverage_target, notes)
VALUES (:name, :target_scope, :class_limit, :coverage_target, :notes)
RETURNING
    id AS run_id,
    name,
    status,
    target_scope,
    class_limit,
    coverage_target;