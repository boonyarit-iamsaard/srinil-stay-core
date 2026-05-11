# First-Party Email Password Authentication

Status: accepted

Srinil Stay Core will own email/password authentication for v1, using short-lived JWT access tokens and opaque rotating
refresh tokens stored as HMAC-SHA-256 hashes in PostgreSQL. This keeps the first product loop self-contained while still
supporting browser clients, logout, refresh-token revocation, and session compromise handling without introducing an
external identity provider or a full OAuth/OIDC authorization server.

## Considered Options

- Keep Spring Security HTTP Basic or generated development credentials: rejected because it does not support public
  registration, browser session behavior, or production-safe user onboarding.
- Use an external identity provider: rejected for v1 because it adds setup, callback, account-linking, and local
  development complexity before the product needs it.
- Use server-side web sessions: rejected because future application APIs should authenticate with bearer access tokens
  rather than cookie-authenticated domain requests.
- Use JWT refresh tokens: rejected because long-lived refresh credentials need server-side revocation, rotation, and
  reuse detection anyway.

## Consequences

- Srinil Stay Core owns password hashing, credential checks, token issuing, refresh-token rotation, and session
  revocation.
- JWT signing and refresh-token hashing use separate secrets.
- Production startup fails fast when JWT signing or refresh-token hashing secrets are missing.
- Local development may use documented local-only defaults for auth secrets.
- The refresh cookie is named `srinil_refresh_token` and scoped to `/api/v1/auth`.
- Refresh tokens rotate on every refresh.
- Access tokens live for 15 minutes; refresh tokens live for 30 days.
- V1 auth endpoints are limited to register, login, refresh, logout, and current identity; current identity is
  `GET /api/v1/me`.
- Logout is refresh-cookie based, not access-token based.
- API JSON fields use camelCase.
- Successful auth responses use a standard `data` wrapper.
- Auth errors use Spring Problem Details with stable machine-readable `code` values.
- Passwords must be 12 to 128 characters, with no composition rules in v1.
- Password hashes use BCrypt with strength 12.
- CSRF tokens are deferred for v1 because domain APIs use bearer tokens, refresh/logout are the only cookie-backed
  operations, refresh cookies use `SameSite=Lax` by default, and CORS must use explicit allowed origins.
- Access tokens carry User identity only and are trusted until expiry in v1.
- Access-token revocation and `jti` tracking are not part of v1.
- Refresh-token reuse invalidates only the affected Authenticated Session.
- Email verification, password reset, password change, rate limiting, logout-all, authenticated-session management,
  roles, and property/team/staff authorization are deferred.
