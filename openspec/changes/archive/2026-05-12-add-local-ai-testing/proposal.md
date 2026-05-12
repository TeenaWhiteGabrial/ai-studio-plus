## Why

The project currently has no shared local testing workflow that covers the three frontends and backend together, while requirements have changed multiple times and the current PRD is reconstructed from code. We need a small, reproducible testing foundation first, then use AI to reduce the cost of producing test assets, writing traditional automation scripts, and analyzing failures.

## What Changes

- Add a local-first testing capability for this monorepo, covering backend contract tests and browser smoke tests for portal, admin, and console.
- Use Playwright as the primary browser automation runner, with deterministic assertions kept in code rather than delegated to AI.
- Introduce AI-assisted testing as a workflow around automation: generating test data ideas, drafting Playwright/JUnit test scripts, expanding edge cases from PRD/code context, and summarizing failures/logs.
- Define a minimum local smoke suite before CI/nightly scheduling is considered.
- Define a test data strategy that avoids depending on uncontrolled production-like database state.
- Preserve existing application behavior; this change introduces test infrastructure and guidance, not user-facing product changes.

## Capabilities

### New Capabilities
- `local-ai-assisted-testing`: Defines how the project locally runs deterministic automation and uses AI to help create, maintain, and analyze test assets.

### Modified Capabilities
- None.

## Impact

- Affected areas: root package scripts/dependencies, existing portal Playwright setup, new or extended Playwright setup for admin and console, backend test structure under `ai-studio-service`, and project testing documentation.
- External behavior: no production API or UI contract changes.
- Dependencies: OpenSpec CLI is installed as a root development dependency; Playwright/JUnit-related dependencies may be reused or added only where needed during implementation.
- Constraints: frontend business API tests must respect the project rule that API boundary fields are `snake_case`, frontend internal models may remain `camelCase`, and page components should not add raw `$fetch` business calls.
