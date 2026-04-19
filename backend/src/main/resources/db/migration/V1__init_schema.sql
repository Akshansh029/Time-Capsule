-- Flyway Migration: V1__init_schema.sql
-- Database: PostgreSQL
-- Project: Time Capsule

-- ============================================================
-- ENUMS
-- ============================================================

CREATE TYPE capsule_status AS ENUM ('LOCKED', 'UNLOCKED');

CREATE TYPE content_type AS ENUM ('TEXT', 'IMAGE', 'FILE');

CREATE TYPE member_role AS ENUM ('OWNER', 'CONTRIBUTOR', 'VIEWER');

-- ============================================================
-- USERS
-- ============================================================

CREATE TABLE users (
                       id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       name         VARCHAR(50)  NOT NULL,
                       email        VARCHAR(100) NOT NULL UNIQUE,
                       password     VARCHAR(255) NOT NULL,
                       created_at   TIMESTAMP    NOT NULL DEFAULT now()
);

-- ============================================================
-- CAPSULES
-- ============================================================

CREATE TABLE capsules (
                          id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          title        VARCHAR(75)      NOT NULL,
                          description  VARCHAR(1000)    NOT NULL,
                          status       capsule_status   NOT NULL DEFAULT 'LOCKED',
                          unlock_date  TIMESTAMP        NOT NULL,
                          is_private   BOOLEAN          NOT NULL DEFAULT TRUE,
                          created_at   TIMESTAMP        NOT NULL DEFAULT now(),
                          owner_id     UUID             NOT NULL,

                          CONSTRAINT fk_capsule_owner FOREIGN KEY (owner_id)
                              REFERENCES users (id)
                              ON DELETE CASCADE
);

CREATE INDEX idx_capsules_owner_id   ON capsules (owner_id);
CREATE INDEX idx_capsules_status     ON capsules (status);
CREATE INDEX idx_capsules_unlock_date ON capsules (unlock_date);

-- ============================================================
-- CAPSULE CONTENTS
-- ============================================================

CREATE TABLE capsule_contents (
                                  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                  capsule_id  UUID         NOT NULL,
                                  type        content_type NOT NULL,
                                  body        TEXT,
                                  file_url    VARCHAR(500),
                                  user_id     UUID         NOT NULL,
                                  added_at    TIMESTAMP    NOT NULL DEFAULT now(),

                                  CONSTRAINT fk_content_capsule FOREIGN KEY (capsule_id)
                                      REFERENCES capsules (id)
                                      ON DELETE CASCADE,

                                  CONSTRAINT fk_content_user FOREIGN KEY (user_id)
                                      REFERENCES users (id)
                                      ON DELETE CASCADE,

    -- Either body or file_url must be present
                                  CONSTRAINT chk_content_not_empty CHECK (
                                      body IS NOT NULL OR file_url IS NOT NULL
                                      )
);

CREATE INDEX idx_contents_capsule_id ON capsule_contents (capsule_id);
CREATE INDEX idx_contents_user_id    ON capsule_contents (user_id);

-- ============================================================
-- CAPSULE MEMBERS
-- ============================================================

CREATE TABLE capsule_members (
                                 id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                                 capsule_id  UUID        NOT NULL,
                                 user_id     UUID        NOT NULL,
                                 role        member_role NOT NULL DEFAULT 'VIEWER',

                                 CONSTRAINT fk_member_capsule FOREIGN KEY (capsule_id)
                                     REFERENCES capsules (id)
                                     ON DELETE CASCADE,

                                 CONSTRAINT fk_member_user FOREIGN KEY (user_id)
                                     REFERENCES users (id)
                                     ON DELETE CASCADE,

    -- A user can only have one role per capsule
                                 CONSTRAINT uq_capsule_member UNIQUE (capsule_id, user_id)
);

CREATE INDEX idx_members_capsule_id ON capsule_members (capsule_id);
CREATE INDEX idx_members_user_id    ON capsule_members (user_id);