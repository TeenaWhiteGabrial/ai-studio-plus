## Purpose
Define the expanded local automation coverage for authenticated frontend route surfaces, local credential loading, deprecated Q&A exclusion, and additional backend API contract boundaries.

## Requirements

### Requirement: Local credential files
The project SHALL support local-only Playwright credential files without committing secrets.

#### Scenario: Test credentials are loaded from local files
- **WHEN** a frontend Playwright suite starts
- **THEN** it MUST load credentials from `tests/e2e/.env.local` or `tests/e2e/.env` when present

#### Scenario: Shell values override local files
- **WHEN** the same credential key exists in the shell environment and in a local env file
- **THEN** the shell environment value MUST take precedence

### Requirement: Authenticated frontend route coverage
The project SHALL provide authenticated route-surface tests for active portal, admin, and console functionality.

#### Scenario: Portal active pages are reachable
- **WHEN** portal credentials are configured
- **THEN** Playwright MUST verify active portal pages such as discover, resources, knowledge, and profile are reachable

#### Scenario: Admin active pages are reachable
- **WHEN** admin credentials are configured
- **THEN** Playwright MUST verify active admin management pages are reachable without login redirects

#### Scenario: Console active pages are reachable
- **WHEN** console credentials are configured
- **THEN** Playwright MUST verify active console work pages are reachable without login redirects

### Requirement: Deprecated Q&A exclusion
The project SHALL not require removed Q&A community functionality in the expanded required test coverage.

#### Scenario: Required coverage is generated
- **WHEN** the test matrix is defined
- **THEN** question/answer community pages MUST be excluded from required pass criteria

### Requirement: Expanded backend contract coverage
The backend SHALL include pure contract tests for additional high-risk frontend/backend boundary DTOs.

#### Scenario: Field aliases are verified
- **WHEN** DTOs accept transitional frontend field names
- **THEN** tests MUST verify accepted aliases deserialize to the expected Java fields

#### Scenario: Required fields are verified
- **WHEN** DTOs define validation constraints
- **THEN** tests MUST verify invalid payloads produce validation violations
