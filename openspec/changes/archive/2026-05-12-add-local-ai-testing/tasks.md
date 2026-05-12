## 1. Baseline Setup

- [x] 1.1 Add or update root-level test scripts that expose local backend, portal, admin, and console test commands.
- [x] 1.2 Document required local prerequisites: Java, Maven, pnpm, browsers, local backend URL, frontend ports, and optional test credentials.
- [x] 1.3 Verify OpenSpec CLI usage is documented for this change so future work can continue through `openspec status` and apply mode.

## 2. Backend Contract Tests

- [x] 2.1 Identify the first backend API slice for local contract coverage, prioritizing login/auth and one content or resource API boundary.
- [x] 2.2 Add contract tests that verify request validation and stable response shape for the selected API slice.
- [x] 2.3 Add at least one test that explicitly verifies `snake_case` JSON field behavior at the backend API boundary.
- [x] 2.4 Ensure backend tests can run locally with a single documented Maven command.

## 3. Portal Playwright Smoke

- [x] 3.1 Review the existing portal Playwright setup and keep the working smoke tests as the baseline.
- [x] 3.2 Add or adjust portal smoke coverage for public home, article/resource navigation, and login entry visibility.
- [x] 3.3 Ensure portal smoke can run locally through the existing portal package script and from a root-level command.

## 4. Admin And Console Playwright Smoke

- [x] 4.1 Add Playwright configuration and scripts to the admin app using the same local pattern as portal.
- [x] 4.2 Add an admin login/page-load smoke test that skips or fails clearly when credentials are not configured.
- [x] 4.3 Add Playwright configuration and scripts to the console app using the same local pattern as portal.
- [x] 4.4 Add a console login/page-load smoke test that skips or fails clearly when credentials are not configured.

## 5. Controlled Fixtures And Data

- [x] 5.1 Define a small local fixture convention for browser route mocks or static test responses where backend integration is not under test.
- [x] 5.2 Document the minimum seeded data or setup-created records needed for real integration smoke tests.
- [x] 5.3 Ensure the first smoke tests do not depend on accidental existing database records.

## 6. AI-Assisted Testing Workflow

- [x] 6.1 Add a concise testing guide that explains how AI should help generate test data, draft automation scripts, and analyze failures.
- [x] 6.2 Add prompt/checklist examples for AI-generated Playwright tests, backend contract tests, and failure summaries.
- [x] 6.3 Document that AI-generated output must become deterministic repository assets before it counts as test coverage.
- [x] 6.4 Add a lightweight note format for recording AI false positives, bad generated tests, or useful corrections.

## 7. Verification

- [x] 7.1 Run the backend test command and record the result.
- [x] 7.2 Run portal smoke locally and record the result.
- [x] 7.3 Run admin smoke locally and record the result.
- [x] 7.4 Run console smoke locally and record the result.
- [x] 7.5 Update the OpenSpec task checklist as items are completed.
