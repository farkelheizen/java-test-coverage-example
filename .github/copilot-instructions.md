# Copilot Instructions for java-test-coverage-example

## Project Context
- This is a Java 17 Maven project focused on generating and improving automated test coverage.
- Production code is under `src/main/java/com/example`.
- Tests are under `src/test/java/com/example`.
- Build and test with Maven.

## General Coding Rules
- Keep changes scoped and minimal to the requested task.
- Preserve existing package structure and naming conventions.
- Prefer clear, readable code and deterministic tests.
- Do not introduce unnecessary dependencies.

## Testing Requirements
Whenever generating or modifying tests in this repository, you MUST adhere to the following rules:

1. **Frameworks:** Use exactly Java 17, JUnit 5 (Jupiter), and Mockito 5.
2. **No JUnit 4:** Never use `org.junit.Test`, `org.junit.Assert`, or `@RunWith`.
3. **Annotations:**
   - Use `@org.junit.jupiter.api.Test`.
   - Use `@org.junit.jupiter.api.extension.ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)` for Mockito.
   - Use `@Mock` for dependencies and `@InjectMocks` for the class under test.
4. **Assertions:** Use `org.junit.jupiter.api.Assertions.*` (e.g., `assertEquals`, `assertThrows`, `assertTrue`).
5. **Coverage:** Your goal is to achieve >70% branch and instruction coverage. Ensure you write tests for edge cases, null inputs, and expected exceptions.
6. **Execution:** Tests must compile and pass using `mvn clean test`.

## Verification Checklist
- New or changed tests compile successfully.
- Test suite passes locally with `mvn clean test`.
- Coverage impact is checked in JaCoCo report (`target/site/jacoco/index.html`).
- No JUnit 4 APIs or annotations are used.
