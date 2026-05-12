## Context

The first testing change established local commands, baseline Playwright smoke tests, and backend contract tests. The project now has local test credentials available, so authenticated page coverage can be added without hardcoding secrets.

## Goals / Non-Goals

**Goals:**

- Cover active portal, admin, and console function surfaces with maintainable authenticated route checks.
- Keep tests robust by verifying page reachability and stable headings/sections rather than brittle implementation details.
- Centralize credential loading so `.env` and `.env.local` files work consistently.
- Add backend contract tests for DTOs that are likely to drift across frontend/backend boundaries.

**Non-Goals:**

- Do not create exhaustive click-by-click CRUD E2E for every form in this iteration.
- Do not restore or cover removed Q&A community functionality as a required test target.
- Do not commit real test credentials.
- Do not require CI/nightly scheduling yet.

## Decisions

- Use route-surface Playwright specs as the main coverage expansion: they are cheaper, stable, and immediately catch auth/router/build regressions.
- Keep destructive or data-mutating flows out of the first broad suite unless fixtures and cleanup are explicit.
- Support both `tests/e2e/.env` and `tests/e2e/.env.local`; shell environment variables still win.
- Keep backend contract tests pure and fast, focused on DTO mapping/validation instead of database state.

## Risks / Trade-offs

- Route coverage can miss deep business bugs -> Mitigate by adding targeted flow specs later for article publish, resource audit, and output reporting.
- Real accounts can have different permissions -> Document role expectations and make failures explicit.
- Pages may load but show empty data -> Treat as acceptable for route coverage unless the test targets a specific data flow.
