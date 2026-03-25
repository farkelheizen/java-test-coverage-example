## How to Kick Off the Autonomous Swarm

The working custom agents now live under `.github/agents/`.

- `coverage-coordinator.agent.md` is the user-invocable entry point.
- `jacoco-coverage-analyst.agent.md` is a hidden subagent for report analysis and batching.
- `java-test-writer.agent.md` is a hidden subagent for generating and repairing tests.

## Invocation Checklist

Before invoking the coordinator, decide these inputs:

- target package or classes
- class limit `n`
- stopping condition such as `reach 70% coverage`
- whether the run should prioritize classes with no existing tests

### In VS Code Chat

Invoke the top-level agent directly with a bounded class limit:

> `/agent coverage-coordinator Target com.example.util and com.example.service, limit to 8 classes total, stop once both packages exceed 70% coverage.`

You can also target a single package or a specific set of classes:

> `/agent coverage-coordinator Target com.example.service only, limit to 5 classes total, prioritize classes with no matching tests.`

Examples:

```text
/agent coverage-coordinator Target com.example.util only, limit to 3 classes total, prefer classes with no matching tests.
```

```text
/agent coverage-coordinator Target com.example.service.OrderService, com.example.service.PaymentService, and com.example.service.InventoryService, limit to 3 classes total, add edge-case tests if coverage remains below 70%.
```

```text
/agent coverage-coordinator Target src/main/java/com/example/service, limit to 10 classes total, stop after the first successful batch if mvn test fails on later batches.
```

What the coordinator does in VS Code:

1. Parses the requested scope and class limit.
2. Invokes `jacoco-coverage-analyst` to run Maven and inspect JaCoCo output.
3. Chooses the highest-value uncovered classes without exceeding the total limit.
4. Invokes `java-test-writer` in batches.
5. Re-runs Maven and JaCoCo after each batch.

### In a GitHub Issue

If you are using GitHub Copilot coding agents from GitHub, use the same agent name and pass the limit explicitly in the issue body:

> **@copilot** Please invoke `/agent coverage-coordinator`.
>
> Target `com.example.util` and `com.example.service`, limit the run to `8` classes total, stop once those packages exceed `70%` coverage, and ensure the repository passes `mvn clean test` before finishing.

Additional GitHub issue examples:

```text
@copilot Please invoke /agent coverage-coordinator.

Target com.example.service only. Limit the run to 5 classes total. Prioritize classes with no matching tests. Stop when package coverage exceeds 70%.
```

```text
@copilot Please invoke /agent coverage-coordinator.

Target com.example.util and com.example.service. Limit the run to 8 classes total. After each batch, rerun mvn test jacoco:report. If generated tests fail to compile, repair them before continuing.
```

```text
@copilot Please invoke /agent coverage-coordinator.

Target these classes only: com.example.service.OrderService, com.example.service.PaymentService, com.example.service.InventoryService. Limit the run to 3 classes total and stop after all three are covered.
```

Notes for GitHub usage:

- Keep the class limit explicit so the coordinator can enforce it reliably.
- Name packages or fully qualified classes directly in the issue body.
- State whether the stopping condition is package coverage, class completion, or build health.
- If GitHub agent support is not enabled for the repository, use VS Code Chat instead.

### What Happens Next

1. `coverage-coordinator` parses the requested scope and the class limit `n`.
2. It invokes `jacoco-coverage-analyst` to run Maven, read JaCoCo output, and pick the highest-value uncovered classes.
3. It batches those classes without exceeding the total limit.
4. It invokes `java-test-writer` for each batch.
5. After each batch, it reruns Maven and JaCoCo to verify progress or send failures back for repair.

## Recommended Prompt Pattern

Use this structure in either VS Code or GitHub:

```text
Target <package(s) or class(es)>, limit to <n> classes total, stop when <coverage or completion condition>, and ensure mvn clean test passes before finishing.
```

Example:

```text
Target com.example.service and com.example.util, limit to 8 classes total, stop once both packages exceed 70% coverage, and ensure mvn clean test passes before finishing.
```