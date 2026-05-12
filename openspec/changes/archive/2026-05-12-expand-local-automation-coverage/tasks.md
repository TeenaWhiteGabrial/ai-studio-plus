## 1. Credential Loading

- [x] 1.1 Update the Playwright env loader to support both `.env.local` and `.env`.
- [x] 1.2 Update gitignore and docs so real credential files remain local-only.

## 2. Frontend Route Coverage

- [x] 2.1 Add shared Playwright helpers for authenticated login and page reachability checks.
- [x] 2.2 Add portal active route coverage excluding deprecated Q&A requirements.
- [x] 2.3 Add admin active route coverage for dashboard, resources, audit, system, content, message, and output surfaces.
- [x] 2.4 Add console active route coverage for dashboard, article, resource, project, task, stats, messages, and settings surfaces.

## 3. Backend Contract Coverage

- [x] 3.1 Add DTO contract tests for user/project/output/resource-like request boundaries.
- [x] 3.2 Verify alias and `snake_case` behavior for additional high-risk fields.

## 4. Verification

- [x] 4.1 Run backend tests.
- [x] 4.2 Run Playwright list or smoke checks without exposing credentials.
- [x] 4.3 Update the OpenSpec checklist.
