## ADDED Requirements

### Requirement: Local test entry points
The project SHALL provide local commands or documented command paths for running the first automated test suite across backend, portal, admin, and console.

#### Scenario: Developer runs local smoke tests
- **WHEN** a developer follows the documented local test commands
- **THEN** the project runs the configured backend contract tests and frontend smoke tests without requiring CI or nightly infrastructure

#### Scenario: Missing environment is reported clearly
- **WHEN** a required local service, browser dependency, or test account is unavailable
- **THEN** the test command MUST fail with a message that identifies the missing prerequisite rather than producing an ambiguous business failure

### Requirement: Browser smoke coverage
The project SHALL use Playwright for browser-level smoke tests and keep pass/fail assertions deterministic.

#### Scenario: Portal smoke runs
- **WHEN** the portal smoke suite runs locally
- **THEN** it MUST verify that the portal can load core public entry points and expose expected login or content navigation affordances

#### Scenario: Admin smoke runs
- **WHEN** the admin smoke suite runs locally
- **THEN** it MUST verify that the admin app can reach login and at least one stable authenticated management surface when test credentials are configured

#### Scenario: Console smoke runs
- **WHEN** the console smoke suite runs locally
- **THEN** it MUST verify that the console app can reach login and at least one stable authenticated work surface when test credentials are configured

### Requirement: Backend contract coverage
The project SHALL include backend contract tests for high-risk API boundaries before relying on full cross-end E2E flows.

#### Scenario: Request field naming is verified
- **WHEN** backend contract tests cover a request DTO that is consumed by frontend code
- **THEN** the tests MUST verify the accepted JSON field names and prioritize the backend `snake_case` contract

#### Scenario: Response shape is verified
- **WHEN** backend contract tests cover a response consumed by frontend code
- **THEN** the tests MUST verify the stable response envelope and important field names used by the UI

#### Scenario: Validation failure is verified
- **WHEN** a covered endpoint receives an invalid required payload
- **THEN** the tests MUST verify that the endpoint returns the expected validation failure status and error shape

### Requirement: Controlled test data
The project SHALL define a local test data strategy that avoids accidental dependence on uncontrolled database state.

#### Scenario: Frontend-only smoke uses controlled responses
- **WHEN** a frontend smoke test does not need to verify backend integration
- **THEN** it MAY use browser-level route mocks or static fixture responses to keep the test deterministic

#### Scenario: Integration smoke uses explicit setup data
- **WHEN** a smoke test verifies real backend integration
- **THEN** it MUST rely on documented seeded data, setup-created data, or explicitly configured test accounts

### Requirement: AI-assisted test asset workflow
The project SHALL use AI to assist test creation and analysis, while converting useful AI output into deterministic repository assets.

#### Scenario: AI drafts automation
- **WHEN** AI is used to create Playwright, JUnit, MockMvc, or fixture code
- **THEN** the generated output MUST be reviewed, committed as normal code, and runnable without asking AI to decide the final test result

#### Scenario: AI expands test data
- **WHEN** AI proposes test data or edge cases
- **THEN** accepted cases MUST be turned into explicit fixtures, test cases, or checklist items with clear expected outcomes

#### Scenario: AI analyzes failures
- **WHEN** a local test run fails and logs, traces, screenshots, or backend errors are available
- **THEN** AI MAY summarize likely causes and affected areas, but the fix MUST be validated by rerunning deterministic tests

### Requirement: AI reliability tracking
The project SHALL track whether AI assistance is improving testing work rather than creating untrusted noise.

#### Scenario: AI output is corrected
- **WHEN** a developer rejects or substantially corrects AI-generated test code, data, or analysis
- **THEN** the correction SHOULD be recorded in the relevant review notes, prompt notes, or testing documentation so future prompts can improve

#### Scenario: AI analysis is wrong
- **WHEN** AI misclassifies a test failure or suggests an incorrect root cause
- **THEN** the final human-confirmed cause SHOULD be preserved near the test or troubleshooting guide
