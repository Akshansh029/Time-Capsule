-- V4__create_refresh_tokens_table.sql
-- Creates the refresh_tokens table for the hybrid JWT auth approach.
-- Refresh tokens are hashed (SHA-256) before storage — raw tokens are never persisted.
-- Token rotation: each use marks the old token as `used` and issues a new one.
-- Reuse detection: if a used token is seen again, the entire family is invalidated.

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE refresh_tokens (
                                id              UUID        DEFAULT gen_random_uuid()  NOT NULL,
                                token_hash      VARCHAR(64)                            NOT NULL,  -- SHA-256 hex of the raw refresh token
                                user_id         UUID                                   NOT NULL,  -- FK to your users table
                                family_id       UUID                                   NOT NULL,  -- groups the rotation chain; used for reuse detection
                                used            BOOLEAN     DEFAULT FALSE              NOT NULL,  -- flipped to TRUE once this token is rotated out
                                expires_at      TIMESTAMP                              NOT NULL,  -- absolute expiry of this refresh token
                                created_at      TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,

                                CONSTRAINT pk_refresh_tokens
                                    PRIMARY KEY (id),

                                CONSTRAINT uq_refresh_tokens_token_hash
                                    UNIQUE (token_hash),

                                CONSTRAINT fk_refresh_tokens_user
                                    FOREIGN KEY (user_id) REFERENCES users (id)
                                        ON DELETE CASCADE
);

-- Fast lookup by token_hash on every /auth/refresh call
CREATE INDEX idx_refresh_tokens_token_hash ON refresh_tokens (token_hash);

-- Fast family-wide invalidation on reuse detection
CREATE INDEX idx_refresh_tokens_family_id  ON refresh_tokens (family_id);

-- Fast per-user invalidation on "logout all devices"
CREATE INDEX idx_refresh_tokens_user_id    ON refresh_tokens (user_id);