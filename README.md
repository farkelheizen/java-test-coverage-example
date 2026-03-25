# java-test-coverage-example

A Java 21 Maven project designed for testing autonomous agentic test-coverage creation.

## Overview

This project contains **100 dummy Java classes** that compile and execute correctly, but intentionally have no test coverage (except a single placeholder test). The purpose is to serve as a target for AI agents to automatically generate meaningful test cases.

## Agent Usage

This repository includes a custom Copilot agent named `coverage-coordinator` under `.github/agents/`.

Quick start in VS Code:

```text
/agent coverage-coordinator Target com.example.service and com.example.util, limit to 8 classes total, stop once both packages exceed 70% coverage.
```

Quick start on GitHub:

```text
@copilot Please invoke /agent coverage-coordinator.

Target com.example.service and com.example.util, limit the run to 8 classes total, stop once those packages exceed 70% coverage, and ensure mvn clean test passes before finishing.
```

Detailed instructions and more examples are in [docs/how-to-kick-off-the-autonomous-swarm.md](docs/how-to-kick-off-the-autonomous-swarm.md).

## Project Structure

```
src/main/java/com/example/
├── enums/          (5 classes)  — OrderStatus, PaymentMethod, Priority, EmployeeStatus, ProductCategory
├── model/          (35 classes) — Bean-style POJOs: Person, Customer, Order, Product, Invoice, etc.
├── service/        (40 classes) — Business logic with multi-level branching: OrderService, TaxCalculator, etc.
└── util/           (20 classes) — Static utility helpers: StringUtils, DateUtils, MathUtils, etc.
```

## Tech Stack

- **Java 21**
- **JUnit 5** (junit-jupiter 5.10.2) — test framework
- **Mockito** (mockito-junit-jupiter 5.11.0) — mocking framework
- **JaCoCo** (0.8.11) — code coverage reports

## Building

```bash
mvn compile          # compile all sources
mvn test             # run tests (generates JaCoCo report)
mvn verify           # full build + tests
```

JaCoCo coverage report is generated at `target/site/jacoco/index.html` after `mvn test`.

## Purpose

After this project is set up, autonomous AI agents will be used to:
1. Analyze each class and understand its logic
2. Generate JUnit 5 test cases with Mockito mocking where appropriate
3. Achieve high code coverage automatically
