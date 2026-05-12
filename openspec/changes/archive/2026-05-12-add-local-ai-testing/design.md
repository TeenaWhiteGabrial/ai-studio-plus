## Context

AI Studio is a monorepo with three frontends and one Spring Boot backend:

- `apps/ai-studio-portal-web`: Nuxt portal. It already has a Playwright config and a small E2E test base.
- `apps/ai-studio-admin-web`: Vite admin app. It has no Playwright setup yet.
- `apps/ai-studio-console-web`: Vite console app. It has no Playwright setup yet.
- `ai-studio-service`: Spring Boot backend. It has test dependencies but no shared contract-test baseline under `src/test`.

The project also has an important API boundary rule: backend JSON contracts use `snake_case`; frontend internal models may use `camelCase`, but API mapping must happen in a unified layer. Tests added by this change must reinforce that rule rather than bypass it.

## Goals / Non-Goals

**Goals:**

- Establish a local-first test workflow that can run before CI/nightly infrastructure exists.
- Add deterministic automation as the execution layer: backend contract tests and Playwright smoke tests.
- Use AI where it has better leverage: generating test data ideas, drafting traditional automation scripts, expanding edge cases from PRD/code context, and analyzing failures.
- Keep the first suite small enough to run frequently and debug locally.
- Make test data controllable so local runs do not depend on accidental database state.

**Non-Goals:**

- Do not make AI the authority for pass/fail decisions when a deterministic assertion can be written.
- Do not introduce CI/nightly scheduling in the first implementation step.
- Do not require a complete rebuilt PRD before test automation can start.
- Do not remove temporarily retained Q&A community code as part of this testing change.
- Do not test against production data or uncontrolled remote database state.

## Decisions

### Decision 1: Local-first before CI-first

Implement local commands and repeatable local setup first. CI can be added after the suite is stable.

Alternative considered: build nightly smoke tests immediately. This was rejected because the project does not yet have stable test accounts, resettable test data, or equal Playwright coverage across the three frontends.

### Decision 2: Deterministic scripts execute; AI assists around them

Playwright and JUnit/MockMvc-style tests will make the final assertions. AI will help generate scripts, generate or vary test data, suggest edge cases, and summarize failures/logs.

Alternative considered: let AI directly browse the app and decide whether behavior is correct. This can be useful for exploration, but it is not stable enough to be the default regression mechanism.

### Decision 3: Start with smoke and contract coverage, not exhaustive E2E

The first suite covers:

- Portal public smoke: home, articles/resources entry points, login visibility.
- Admin smoke: login and at least one stable management page load.
- Console smoke: login and at least one stable content/resource workflow entry.
- Backend contract tests for high-risk API boundaries, especially request/response field naming and validation.

Alternative considered: implement a full article publish-to-portal workflow first. This is valuable later, but it depends on stable test data and cross-role accounts. The first version should prove the harness before expanding flow depth.

### Decision 4: Use controlled fixtures with two layers

Use two fixture styles:

- Pure local/browser fixtures for frontend smoke where full backend state is not required.
- Minimal seeded or setup-created backend data for contract and cross-end smoke tests.

Alternative considered: rely on whatever data exists in the local database. This is fast at first but makes failures non-reproducible and will damage trust in the test suite.

### Decision 5: Treat AI output as reviewable test assets

AI-generated tests or data cases must become normal repository artifacts before they count: reviewed, committed, runnable, and deterministic.

Alternative considered: keep AI prompts as the only source of test behavior. This was rejected because prompts are hard to diff, hard to review, and can drift when model behavior changes.

## Risks / Trade-offs

- AI-generated scripts may look correct but assert the wrong business meaning → Keep human review in the loop and require deterministic local execution before accepting tests.
- Browser smoke tests may become flaky if they depend on remote services or timing → Prefer stable selectors, controlled data, and narrow smoke scope.
- Backend contract tests may collide with database state → Start with pure controller/request mapping tests where possible, then add database-backed tests only after fixture strategy is clear.
- Adding Playwright to all apps increases setup cost → Reuse one shared pattern and keep first coverage intentionally small.
- Local-first testing does not immediately protect shared branches → Add CI only after local suite has a stable signal.
