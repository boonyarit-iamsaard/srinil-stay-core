# Srinil Stay Core

Core backend context for Srinil Stay. This context owns authentication and the durable records needed by the application API.

## Language

**User**:
A person who can authenticate to Srinil Stay Core.
_Avoid_: Account, guest, staff member

**Email Identity**:
A normalized email address that identifies a **User** for authentication.
_Avoid_: Raw email, login name

**Authenticated Session**:
A continuous login for one **User**.
_Avoid_: Token family, login token

## Relationships

- A **User** may have zero or more authenticated sessions.
- A **User** has exactly one **Email Identity**.
- An **Authenticated Session** belongs to exactly one **User**.
- Public registration creates a **User** and immediately creates an **Authenticated Session**.
- Failed public registration does not leave behind a **User** without an initial **Authenticated Session**.
- Successful registration and login return the **User** summary with access-token details.
- A **User** summary contains id, email, email verified time, and disabled time.
- A **User**'s last login time changes when credentials are accepted, not when an **Authenticated Session** is refreshed.
- Public registration does not create stay-domain objects in v1.
- Duplicate public registration returns a specific email-already-registered outcome.
- In v1, protected application data is owned by exactly one **User**.
- A disabled **User** cannot create or extend an **Authenticated Session**.
- Disabled **User** login returns the generic invalid-credentials outcome.
- Disabled **User** refresh returns the generic invalid-refresh-token outcome and invalidates the current
  **Authenticated Session**.
- Refresh-token reuse invalidates only the affected **Authenticated Session**.
- Refresh-token reuse returns the same public invalid-refresh-token outcome as other refresh failures.
- Refresh-token rotation is strict single-use with no grace window for concurrent duplicate refresh requests.
- Logout invalidates only the current **Authenticated Session**.
- Logout succeeds and clears the refresh cookie even when no valid **Authenticated Session** can be found.
- Failed login returns a generic invalid-credentials outcome.
- Failed session refresh returns a generic invalid-refresh-token outcome.
- Expired **Authenticated Sessions** are rejected without being marked as revoked.
- Current identity reads load the **User** from durable storage.
- Current identity reads fail when the **User** is disabled.
- In v1, protected domain requests trust valid access tokens until they expire.
- Access tokens carry **User** identity only, not domain authorization claims.
- User agent and IP address are non-authoritative **Authenticated Session** metadata in v1.
- Missing or malformed user agent and IP address metadata do not block registration or login.
- User agent and IP address changes do not block refresh in v1.
- Refreshing an **Authenticated Session** keeps its original user agent and IP address metadata.
- Email verification is not part of the current authentication scope.

## Example dialogue

> **Dev:** "When a **User** logs in from a new device, do we create a new authenticated session?"
> **Domain expert:** "Yes — each device login should be tracked independently."

## Flagged ambiguities

- "account" is not currently a domain term; use **User** for the authenticating actor.
- Email verification exists as a future capability, but current authentication should not require it.
- Property, team, staff, and organization authorization are deferred; use **User** ownership for v1.
- In v1, disabling a **User** does not revoke already-issued access tokens before they expire.
- In v1, disabling a **User** does not proactively revoke all **Authenticated Sessions**.
- Compromise of one **Authenticated Session** does not imply every session for the **User** is compromised.
- **Email Identity** is normalized by trimming surrounding whitespace and lowercasing before registration and login lookup.
- Duplicate registration may reveal that an **Email Identity** already exists; this is accepted for v1 because email
  verification and recovery infrastructure are deferred.

## Checkpoint

Last reviewed: 2026-05-14.

Current authentication implementation:

- `users` and `refresh_tokens` tables exist through Flyway.
- `UserEntity` and `RefreshTokenEntity` exist.
- `UserRepository` and `RefreshTokenRepository` exist.
- `spring-boot-starter-oauth2-resource-server` is available for the later JWT resource-server slice.
- Auth configuration properties exist for JWT, refresh tokens, cookies, password hashing, and CORS.
- Email normalization, password encoding, JWT issuing, refresh-token generation, and refresh-token hashing helpers exist.
- Runtime security still uses Spring Security HTTP Basic with Spring Boot's generated development user.
- `./mvnw test` passes.
- `./mvnw spotless:check` passes.

Resolved design direction:

- Use a lean first-party authentication baseline for Srinil Stay Core.
- Auth baseline ADR: `docs/adr/0001-first-party-email-password-authentication.md`.
- Use backend-owned email/password authentication.
- Use short-lived JWT access tokens.
- Use opaque rotating refresh tokens stored as hashes in PostgreSQL.
- Use refresh cookie name `srinil_refresh_token`.
- Treat logout as idempotent: it clears the refresh cookie even when no valid **Authenticated Session** is found.
- Public registration immediately creates an **Authenticated Session** and returns the **User** summary with access-token
  details.
- Duplicate public registration returns a specific email-already-registered outcome.
- Current identity reads load the **User** from durable storage and fail when the **User** is disabled.
- A **User** summary uses timestamp fields for state transitions: `emailVerifiedAt` and `disabledAt`, both explicitly
  present as null when absent.
- Auth success responses for registration and login include `accessToken`, `tokenType: "Bearer"`, `expiresIn`, and the
  **User** summary.
- `expiresIn` is represented as integer seconds in access-token responses.
- Refresh success responses include new access-token details only, not the **User** summary.
- Registration returns `201 Created`; login, refresh, and current identity return `200 OK`.
- Logout returns `204 No Content` after clearing the refresh cookie.
- Auth success responses use a `data` wrapper except logout, which has no response body.
- The v1 success response wrapper is exactly `{ "data": ... }` with no generic metadata fields.
- Auth request and response DTOs are Java records in v1.
- Auth failures use Problem Details with stable machine-readable `code` values and no `data` wrapper.
- V1 auth endpoints are `POST /api/v1/auth/register`, `POST /api/v1/auth/login`,
  `POST /api/v1/auth/refresh`, `POST /api/v1/auth/logout`, and `GET /api/v1/me`.
- V1 has no committed frontend origin yet; an empty CORS allowlist is valid.
- Browser credentialed CORS uses explicit allowed origins only, never wildcard origins.
- CSRF tokens are deferred in v1.
- Registration and login accept `email` and `password` JSON fields.
- Registration and login normalize **Email Identity** by trimming surrounding whitespace and lowercasing.
- Registration validates email format after trimming and requires passwords from 12 to 128 characters.
- Login validates that email and password are present, but credential failures share the generic invalid-credentials outcome.
- Unknown JSON request fields are ignored in v1.
- Refresh and logout read the refresh token only from the `srinil_refresh_token` cookie.
- Refresh and logout do not accept refresh tokens in JSON request bodies or `Authorization` headers.
- Duplicate registration returns `EMAIL_ALREADY_REGISTERED` with a specific public message.
- Login failures return `INVALID_CREDENTIALS` with a generic public message.
- Unknown-email login attempts perform dummy password verification before returning the generic invalid-credentials
  outcome.
- Disabled-**User** login attempts do not reveal whether the password was correct.
- Refresh failures return `INVALID_REFRESH_TOKEN` with a generic public message.
- Current identity authentication failures return `UNAUTHENTICATED` with a generic public message.
- Field validation failures return `VALIDATION_FAILED` and may expose field-specific validation details.
- Validation failures return `400 Bad Request` Problem Details with `code=VALIDATION_FAILED`.
- Validation Problem Details may include a `fields` list with field names and public validation messages.
- Malformed JSON returns `VALIDATION_FAILED` without field details.
- Rate limiting and account lockout remain deferred in v1.
- A future rate limit may return `429 Too Many Requests` with a `RATE_LIMITED` code.
- V1 auth observability uses structured application logs, not a separate audit table.
- Auth logs must not include raw passwords, raw refresh tokens, access tokens, password hashes, or full request bodies.
- Refresh-token reuse is logged as a warning because it may indicate **Authenticated Session** compromise.
- Internal refresh-token revocation reasons are `ROTATED`, `LOGOUT`, `REUSE_DETECTED`, and `USER_DISABLED`.
- Refresh-token revocation reasons are not exposed in public Problem Details.
- Refresh tokens are only returned as HTTP-only cookies, never in JSON response bodies.
- Refresh cookies are host-scoped, HTTP-only, `SameSite=Lax`, and scoped to `/api/v1/auth`.
- Refresh cookies use `Secure` outside local development; the local profile may disable `Secure` for plain HTTP.
- Register, login, and successful refresh set the refresh cookie for the **Authenticated Session** lifetime.
- Logout and failed refresh clear the refresh cookie with `Max-Age=0`.
- Successful refresh rotates the refresh token and sets a replacement refresh cookie.
- Failed refresh clears the refresh cookie and returns the generic invalid-refresh-token outcome.
- Refresh-token reuse does not invalidate other **Authenticated Sessions** for the same **User**.
- Refreshing an **Authenticated Session** keeps the original user agent and IP address metadata.
- Expired **Authenticated Sessions** are rejected without being marked as revoked.
- `lastLoginAt` changes when credentials are accepted through registration or password login, not refresh.
- Registration, login, refresh, and logout persist their auth state changes transactionally.
- The next auth slice keeps the current simple package layout rather than introducing deeper architectural layers.
- Defer email verification enforcement, roles, staff/guest/property ownership, password reset, and password change.

Completed design grill:

- Auth API contract is complete enough to start the next implementation slice.
- Remaining choices are implementation details: exact Java type names, cookie helper placement, and exception class
  boundaries.

Completed implementation slices:

1. Add `spring-boot-starter-oauth2-resource-server`.
2. Add `UserRepository`.
3. Add `RefreshTokenRepository`.
4. Add focused repository/entity tests.
5. Keep current `SecurityConfig` until login, refresh, token, and controller services are ready to replace HTTP Basic fully.
6. Add auth configuration properties and production secret validation.
7. Add email normalization and password encoder infrastructure.
8. Add JWT issuance support.
9. Add refresh-token hashing/generation support.
10. Add focused service/config tests.

Recommended next implementation slice:

1. Add Java-record request/response DTOs and a minimal `ApiResponse`.
2. Add auth exception types and Problem Details handling for accepted auth error codes.
3. Add `RefreshTokenService` for create, rotate, revoke, and reuse detection.
4. Add `AuthService` for register, login, refresh, and logout orchestration.
5. Keep controller endpoints and `SecurityConfig` replacement for the following endpoint slice.
