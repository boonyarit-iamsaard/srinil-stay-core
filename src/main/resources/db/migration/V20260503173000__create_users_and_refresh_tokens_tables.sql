CREATE TABLE users
(
    id uuid PRIMARY KEY,
    email varchar(320) NOT NULL,
    password_hash varchar(255) NOT NULL,
    email_verified_at timestamptz NULL,
    disabled_at timestamptz NULL,
    last_login_at timestamptz NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE refresh_tokens
(
    id uuid PRIMARY KEY,
    user_id uuid NOT NULL,
    token_hash varchar(255) NOT NULL,
    family_id uuid NOT NULL,
    expires_at timestamptz NOT NULL,
    revoked_at timestamptz NULL,
    revocation_reason varchar(64) NULL,
    replaced_by_token_id uuid NULL,
    user_agent varchar(512) NULL,
    ip_address varchar(45) NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT fk_refresh_tokens_user_id FOREIGN KEY (
        user_id
    ) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_refresh_tokens_replaced_by_token_id FOREIGN KEY (
        replaced_by_token_id
    ) REFERENCES refresh_tokens (id),
    CONSTRAINT uk_refresh_tokens_token_hash UNIQUE (token_hash)
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);
CREATE INDEX idx_refresh_tokens_family_id ON refresh_tokens (family_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens (expires_at);
