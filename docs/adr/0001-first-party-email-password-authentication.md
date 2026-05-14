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
- Refresh cookies are HTTP-only, host-scoped, and `SameSite=Lax`.
- Refresh cookies use `Secure` by default; local development may disable `Secure` for plain HTTP.
- Register, login, and successful refresh set a refresh cookie with the refresh-token lifetime.
- Logout and failed refresh clear the refresh cookie with `Max-Age=0`.
- Refresh and logout read the refresh token only from the refresh cookie, never from JSON request bodies or
  `Authorization` headers.
- Refresh tokens rotate on every refresh.
- Access tokens live for 15 minutes; refresh tokens live for 30 days.
- V1 auth endpoints are `POST /api/v1/auth/register`, `POST /api/v1/auth/login`,
  `POST /api/v1/auth/refresh`, `POST /api/v1/auth/logout`, and `GET /api/v1/me`.
- Logout is refresh-cookie based, not access-token based.
- API JSON fields use camelCase.
- Successful auth responses use a standard `data` wrapper.
- Access-token responses represent `expiresIn` as integer seconds.
- Registration returns `201 Created`; login, refresh, and current identity return `200 OK`.
- Logout returns `204 No Content` and no response body.
- Auth errors use Spring Problem Details with stable machine-readable `code` values.
- Validation failures return `400 Bad Request` Problem Details with `code=VALIDATION_FAILED` and may include field
  details.
- Duplicate registration intentionally exposes `EMAIL_ALREADY_REGISTERED` with a specific public message.
- Login, refresh, and current-identity failures use generic public messages to avoid additional enumeration paths.
- Login performs dummy password verification for unknown emails to reduce obvious timing differences.
- Disabled-User login attempts do not reveal whether the password was correct.
- Registration and login accept `email` and `password` JSON fields.
- Registration validates email format after trimming and requires passwords from 12 to 128 characters.
- Login keeps validation minimal so credential failures share the generic invalid-credentials outcome.
- Unknown JSON request fields are ignored in v1.
- Passwords must be 12 to 128 characters, with no composition rules in v1.
- Password hashes use BCrypt with strength 12.
- Registration, login, refresh, and logout persist their auth state changes transactionally.
- Failed registration does not leave behind a User without an initial Authenticated Session.
- CSRF tokens are deferred for v1 because domain APIs use bearer tokens, refresh/logout are the only cookie-backed
  operations, refresh cookies use `SameSite=Lax` by default, and CORS must use explicit allowed origins.
- Srinil Stay does not have a committed frontend origin yet, so an empty CORS allowlist is valid.
- Browser credentialed CORS uses explicit allowed origins only; wildcard origins are not used with credentials.
- Access tokens carry User identity only and are trusted until expiry in v1.
- Access-token revocation and `jti` tracking are not part of v1.
- Disabled Users cannot log in or refresh; current identity reads reject disabled Users by loading durable storage.
- Disabling a User does not proactively revoke all Authenticated Sessions or existing access tokens in v1.
- Refresh-token reuse invalidates only the affected Authenticated Session and returns the generic invalid-refresh-token
  outcome.
- Refresh-token rotation is strict single-use with no grace window for concurrent duplicate refresh requests.
- User-agent and IP address metadata are best-effort and non-authoritative in v1.
- Internal refresh-token revocation reasons are `ROTATED`, `LOGOUT`, `REUSE_DETECTED`, and `USER_DISABLED`; these are
  not exposed in public Problem Details.
- Email verification, password reset, password change, rate limiting, logout-all, authenticated-session management,
  roles, and property/team/staff authorization are deferred.
- A future app-level throttle may add `429 Too Many Requests` with a `RATE_LIMITED` Problem Details code.
- V1 auth observability uses structured application logs rather than a separate audit table.
- Auth logs do not include raw credentials, raw tokens, token hashes, or full request bodies.
