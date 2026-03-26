## How to Kick Off the Autonomous Swarm

The working custom agents now live under `.github/agents/`.

- `coverage-coordinator.agent.md` is the user-invocable shared-workspace entry point.
- `coverage-coordinator-isolated.agent.md` is the user-invocable worktree-first entry point.
- `jacoco-coverage-analyst.agent.md` is a hidden, read-only subagent for report analysis and batching.
- `java-test-writer.agent.md` is a hidden subagent for generating and repairing tests.

The coordinator now runs JaCoCo once per run, then fans out one or more analyst subagents against disjoint slices of the requested scope before selecting batches for test generation.

## Invocation Checklist

Before invoking the coordinator, decide these inputs:

- target package or classes
- class limit `n`
- stopping condition such as `reach 70% coverage`
- whether the run should prioritize classes with no existing tests
- whether the coordinator should create an isolated worktree first
- an explicit worktree path if you do not want the coordinator to derive one

### In VS Code Chat

Choose the entry point based on isolation needs:

- Use `coverage-coordinator` for normal shared-workspace runs.
- Use `coverage-coordinator-isolated` when the prompt mentions isolated runs, worktree-first execution, or create/reuse worktree behavior.

Invoke the top-level agent directly with a bounded class limit:

> `/agent coverage-coordinator Target com.example.util and com.example.service, limit to 8 classes total, stop once both packages exceed 70% coverage.`

Invoke the isolated entry point when you want a worktree-first run by design:

> `/agent coverage-coordinator-isolated Target com.example.service only, create or reuse a worktree at ../java-test-coverage-service, limit to 5 classes total, stop once package coverage exceeds 70%.`

You can also target a single package or a specific set of classes:

> `/agent coverage-coordinator Target com.example.service only, limit to 5 classes total, prioritize classes with no matching tests.`

If you want the coordinator to create an isolated worktree first, say so explicitly:

> `/agent coverage-coordinator Create a new worktree at ../java-test-coverage-service, target com.example.service only, limit to 5 classes total, stop once package coverage exceeds 70%.`

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

Likely multi-child analysis example:

```text
/agent coverage-coordinator Target com.example.service and com.example.util, limit to 24 classes total, split broad scopes into parallel analyst slices when useful, stop once both packages exceed 75% coverage, and ensure mvn clean test passes before finishing.
```

That prompt gives the coordinator enough scope and class budget to justify splitting the analysis into multiple analyst subagents, typically by package or class slices.

Likely multi-child analysis example using 24 fully qualified classes:

```text
/agent coverage-coordinator \
	Target com.example.service.AuditService, com.example.service.RecommendationService, \
	com.example.service.DecisionTree, com.example.service.SubscriptionService, \
	com.example.service.EmailService, com.example.service.PaginationService, \
	com.example.service.UserAccountService, com.example.service.ShippingRateCalculator, \
	com.example.service.AuthenticationService, com.example.service.StringProcessingService, \
	com.example.service.SearchService, com.example.service.CustomerService, \
	com.example.service.TaxCalculator, com.example.service.PaymentService, \
	com.example.service.InventoryService, com.example.service.NotificationService, \
	com.example.service.OrderService, com.example.service.InsurancePremiumCalculator, \
	com.example.service.CustomerSupportService, com.example.service.FileProcessingService, \
	com.example.service.GeographyService, com.example.service.ProductService, \
	com.example.service.RiskAssessmentService, and com.example.service.StatisticsService, \
	limit to 24 classes total, \
	split broad scopes into parallel analyst slices when useful, \
	stop once the selected classes exceed 75% coverage, and \
	ensure mvn clean test passes before finishing.
```

What the coordinator does in VS Code:

1. Parses the requested scope and class limit.
2. Optionally creates or reuses an isolated worktree when explicitly requested.
3. Runs `mvn clean test jacoco:report` once when practical.
4. Splits broader scopes into disjoint slices and invokes one or more `jacoco-coverage-analyst` subagents.
5. Chooses the highest-value uncovered classes without exceeding the total limit.
6. Invokes `java-test-writer` in batches.
7. Re-runs Maven and JaCoCo after each batch.

Important limitation:

- The coordinator can create the worktree automatically.
- The current chat session may not be able to relocate into that new worktree automatically.
- When relocation is not possible, the coordinator should stop after worktree creation and return the exact next invocation to run from the new worktree.

Expected handoff format when relocation is not possible:

```text
Worktree created: <path>
Next command: cd <path>
Next chat prompt: /agent <coverage-coordinator or coverage-coordinator-isolated> <rerun prompt>
```

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

```text
@copilot Please invoke /agent coverage-coordinator.

Target src/main/java/com/example/service and src/main/java/com/example/util. Limit the run to 24 classes total. Split broad scopes into parallel analyst slices when useful. Stop once both directories exceed 75% coverage. Ensure mvn clean test passes before finishing.
```

Notes for GitHub usage:

- Keep the class limit explicit so the coordinator can enforce it reliably.
- Name packages or fully qualified classes directly in the issue body.
- State whether the stopping condition is package coverage, class completion, or build health.
- If GitHub agent support is not enabled for the repository, use VS Code Chat instead.

## Parallel Invocation Patterns

There are two safe ways to scale the swarm.

### One Coordinator, Multiple Analysts

Use a single coordinator run when you want one bounded execution that shares a single JaCoCo report and one global class limit.

Example:

```text
/agent coverage-coordinator Target com.example.service and com.example.util, limit to 8 classes total, stop once both packages exceed 70% coverage.
```

The coordinator will decide whether the scope is broad enough to split into multiple analyst slices. This is the preferred pattern for one workspace because the analysts are read-only and the coordinator keeps control of the shared class limit.

### Multiple Coordinator Runs

Use multiple top-level coordinator runs only when each run owns a disjoint scope and a separate working tree.

Recommended setup:

```text
git worktree add ../java-test-coverage-service HEAD
git worktree add ../java-test-coverage-util HEAD
```

Then run one coordinator in each worktree with non-overlapping targets.

Example run 1:

```text
/agent coverage-coordinator Target com.example.service only, limit to 5 classes total, stop once package coverage exceeds 70%.
```

Example run 2:

```text
/agent coverage-coordinator Target com.example.util only, limit to 5 classes total, stop once package coverage exceeds 70%.
```

Do not run multiple coordinators against the same workspace folder when they might write tests into overlapping packages.

You can also ask the coordinator to perform the first worktree-creation step for a single isolated run:

```text
/agent coverage-coordinator Create a new worktree at ../java-test-coverage-service, target com.example.service only, limit to 5 classes total, stop once package coverage exceeds 70%.
```

If the coordinator cannot continue from the new worktree automatically, rerun the returned prompt from that worktree.

If you prefer a dedicated entry point instead of an optional mode on the main coordinator, use:

```text
/agent coverage-coordinator-isolated Target com.example.service only, create or reuse a worktree at ../java-test-coverage-service, limit to 5 classes total, stop once package coverage exceeds 70%.
```

Large isolated-run example that would likely spawn multiple analyst children after worktree creation:

```text
/agent coverage-coordinator-isolated Target com.example.service and com.example.util, create or reuse a worktree at ../java-test-coverage-wide, limit to 24 classes total, stop once both packages exceed 75% coverage, and ensure mvn clean test passes before finishing.
```

Large isolated-run example using 24 fully qualified classes:

```text
/agent coverage-coordinator-isolated \
	Target com.example.service.AuditService, com.example.service.RecommendationService, \
	com.example.service.DecisionTree, com.example.service.SubscriptionService, \
	com.example.service.EmailService, com.example.service.PaginationService, \
	com.example.service.UserAccountService, com.example.service.ShippingRateCalculator, \
	com.example.service.AuthenticationService, com.example.service.StringProcessingService, \
	com.example.service.SearchService, com.example.service.CustomerService, \
	com.example.service.TaxCalculator, com.example.service.PaymentService, \
	com.example.service.InventoryService, com.example.service.NotificationService, \
	com.example.service.OrderService, com.example.service.InsurancePremiumCalculator, \
	com.example.service.CustomerSupportService, com.example.service.FileProcessingService, \
	com.example.service.GeographyService, com.example.service.ProductService, \
	com.example.service.RiskAssessmentService, and com.example.service.StatisticsService, \
	create or reuse a worktree at ../java-test-coverage-wide, \
	limit to 24 classes total, \
	split broad scopes into parallel analyst slices when useful, \
	stop once the selected classes exceed 75% coverage, and \
	ensure mvn clean test passes before finishing.
```

### Nested Subagents

If you want subagents to invoke further subagents, enable the VS Code setting `chat.subagents.allowInvocationsFromSubagents`. That setting is not required for the default coordinator workflow in this repository.

### What Happens Next

1. `coverage-coordinator` parses the requested scope and the class limit `n`.
2. If requested, it creates or reuses a worktree and either continues there or returns the next prompt to run from that location.
3. It runs JaCoCo once and, for broader scopes, fans out one or more `jacoco-coverage-analyst` subagents across disjoint slices.
4. It merges the analyst results and batches those classes without exceeding the total limit.
5. It invokes `java-test-writer` for each batch.
6. After each batch, it reruns Maven and JaCoCo to verify progress or send failures back for repair.

## Recommended Prompt Pattern

Use this structure in either VS Code or GitHub:

```text
Target <package(s) or class(es)>, limit to <n> classes total, stop when <coverage or completion condition>, and ensure mvn clean test passes before finishing.
```

For an isolated run with automatic worktree creation, use this structure:

```text
Create a new worktree at <path>, target <package(s) or class(es)>, limit to <n> classes total, stop when <coverage or completion condition>, and ensure mvn clean test passes before finishing.
```

For a dedicated worktree-first agent, use this structure:

```text
Target <package(s) or class(es)>, create or reuse a worktree at <path>, limit to <n> classes total, stop when <coverage or completion condition>, and ensure mvn clean test passes before finishing.
```

For broader scopes where analyst fan-out is useful, be explicit that the coordinator may split the analysis:

```text
Target <package(s) or class(es)>, limit to <n> classes total, split broad scopes into parallel analyst slices when useful, stop when <coverage or completion condition>, and ensure mvn clean test passes before finishing.
```

Example:

```text
Target com.example.service and com.example.util, limit to 8 classes total, stop once both packages exceed 70% coverage, and ensure mvn clean test passes before finishing.
```

Example with explicit analyst fan-out:

```text
Target com.example.service and com.example.util, limit to 8 classes total, split broad scopes into parallel analyst slices when useful, stop once both packages exceed 70% coverage, and ensure mvn clean test passes before finishing.
```

Example with automatic worktree creation:

```text
Create a new worktree at ../java-test-coverage-service, target com.example.service only, limit to 5 classes total, stop once package coverage exceeds 70% coverage, and ensure mvn clean test passes before finishing.
```

Example with the dedicated isolated agent:

```text
Target com.example.service only, create or reuse a worktree at ../java-test-coverage-service, limit to 5 classes total, stop once package coverage exceeds 70%, and ensure mvn clean test passes before finishing.
```

Example that would likely spawn multiple analyst children:

```text
Target com.example.service and com.example.util, limit to 24 classes total, split broad scopes into parallel analyst slices when useful, stop once both packages exceed 75% coverage, and ensure mvn clean test passes before finishing.
```

Example using 24 fully qualified classes that would likely spawn multiple analyst children:

```text
Target com.example.service.AuditService, com.example.service.RecommendationService,
com.example.service.DecisionTree, com.example.service.SubscriptionService,
com.example.service.EmailService, com.example.service.PaginationService,
com.example.service.UserAccountService, com.example.service.ShippingRateCalculator,
com.example.service.AuthenticationService, com.example.service.StringProcessingService,
com.example.service.SearchService, com.example.service.CustomerService,
com.example.service.TaxCalculator, com.example.service.PaymentService,
com.example.service.InventoryService, com.example.service.NotificationService,
com.example.service.OrderService, com.example.service.InsurancePremiumCalculator,
com.example.service.CustomerSupportService, com.example.service.FileProcessingService,
com.example.service.GeographyService, com.example.service.ProductService,
com.example.service.RiskAssessmentService, and com.example.service.StatisticsService,
limit to 24 classes total,
split broad scopes into parallel analyst slices when useful,
stop once the selected classes exceed 75% coverage, and
ensure mvn clean test passes before finishing.
```

**Troubleshooting**

- Agent selection looks wrong: use `coverage-coordinator` for shared-workspace runs and `coverage-coordinator-isolated` for worktree-first runs. If your prompt starts with "create or reuse a worktree" or "isolated run," the isolated agent is the better match.
- Worktree path collision: if the requested path already exists and you did not ask for reuse, the coordinator should stop and ask for a new path or explicit reuse permission.
- Worktree created but execution stops: use the returned handoff block exactly as written. Run the `cd` command first, then rerun the returned `/agent ...` prompt from that worktree.
- Parallel writers stepping on each other: use separate worktrees and keep package scopes disjoint.
- Nested subagents do not run: enable the VS Code setting `chat.subagents.allowInvocationsFromSubagents` only if you need subagents to spawn their own children. The default coordinator workflow does not require it.