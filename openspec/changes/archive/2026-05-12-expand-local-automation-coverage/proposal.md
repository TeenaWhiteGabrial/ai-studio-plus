## Why

The local AI-assisted testing foundation is in place, and real test accounts are now available. We should expand from smoke-only checks to broader automated coverage of the project's active business surfaces while keeping tests stable and maintainable.

## What Changes

- Expand Playwright coverage from login smoke to authenticated route and feature-surface coverage for portal, admin, and console.
- Add reusable Playwright helpers for environment loading, login, and route checks.
- Expand backend contract tests for high-risk request DTOs and JSON field naming.
- Keep deprecated Q&A/community functionality out of the required coverage set.
- Continue to avoid committing real credentials or relying on uncontrolled database records.

## Capabilities

### New Capabilities
- `expanded-local-automation-coverage`: Defines broader local automated coverage for active project functionality.

### Modified Capabilities
- None.

## Impact

- Affected areas: Playwright tests and helpers under the three frontend apps, local test credential loading, backend contract tests, and testing documentation.
- External behavior: no user-facing product behavior changes.
- Dependencies: reuse existing Playwright and backend test dependencies.
- Constraints: real credentials must remain local-only and ignored by git.
