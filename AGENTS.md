# AI-Studio Project Collaboration Rules

## API Field Naming (Long-Term Rule)

- Backend JSON contract uses `snake_case` as the source of truth.
- Current backend config: `ai-studio-service/src/main/resources/application.yml`
  - `spring.jackson.property-naming-strategy: SNAKE_CASE`

## Required Practice

1. Frontend internal model can use `camelCase`, but request/response at API boundary must be adapted.
2. Do not rely on implicit Jackson mapping for mixed naming.
3. For backend request DTOs that need compatibility, use `@JsonAlias` to accept both naming styles during transition.
4. New APIs should document request/response field names in `snake_case`.

## Implementation Guidelines

1. Frontend must go through a unified API/composable layer for field mapping.
2. Avoid direct raw `$fetch` calls in page components for business APIs.
3. When adding or changing API fields, update both:
   - frontend mapper (snake_case <-> camelCase)
   - backend DTO annotations/validation

## PR Checklist (Must Pass)

1. Request payload fields are verified against backend DTO names.
2. Response fields are normalized before UI rendering.
3. At least one integration/E2E case covers the changed API path.
