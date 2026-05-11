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
- Public registration does not create stay-domain objects in v1.
- Duplicate public registration returns a specific email-already-registered outcome.
- In v1, protected application data is owned by exactly one **User**.
- A disabled **User** cannot create or extend an **Authenticated Session**.
- Refresh-token reuse invalidates only the affected **Authenticated Session**.
- Logout invalidates only the current **Authenticated Session**.
- Failed login returns a generic invalid-credentials outcome.
- Failed session refresh returns a generic invalid-refresh-token outcome.
- Current identity reads load the **User** from durable storage.
- In v1, protected domain requests trust valid access tokens until they expire.
- Access tokens carry **User** identity only, not domain authorization claims.
- User agent and IP address are non-authoritative **Authenticated Session** metadata in v1.
- Email verification is not part of the current authentication scope.

## Example dialogue

> **Dev:** "When a **User** logs in from a new device, do we create a new authenticated session?"
> **Domain expert:** "Yes — each device login should be tracked independently."

## Flagged ambiguities

- "account" is not currently a domain term; use **User** for the authenticating actor.
- Email verification exists as a future capability, but current authentication should not require it.
- Property, team, staff, and organization authorization are deferred; use **User** ownership for v1.
- In v1, disabling a **User** does not revoke already-issued access tokens before they expire.
- Compromise of one **Authenticated Session** does not imply every session for the **User** is compromised.
- **Email Identity** is normalized by trimming surrounding whitespace and lowercasing before registration and login lookup.
- Duplicate registration may reveal that an **Email Identity** already exists; this is accepted for v1 because email
  verification and recovery infrastructure are deferred.

## Checkpoint

Last reviewed: 2026-05-11.

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
- Defer email verification enforcement, roles, staff/guest/property ownership, password reset, and password change.

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

1. Add request/response DTOs and `ApiResponse`.
2. Add auth exception types and Problem Details handling.
3. Add `RefreshTokenService` for create, rotate, revoke, and reuse detection.
4. Add `AuthService` for register, login, refresh, and logout orchestration.
5. Keep `SecurityConfig` replacement for the endpoint/controller slice.
