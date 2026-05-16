-- Flyway Migration: V6__convert_to_timestamptz.sql
-- Converts all TIMESTAMP columns to TIMESTAMPTZ for absolute time tracking.

-- Users table
ALTER TABLE users ALTER COLUMN created_at TYPE TIMESTAMPTZ;

-- Capsules table
ALTER TABLE capsules ALTER COLUMN unlock_date TYPE TIMESTAMPTZ;
ALTER TABLE capsules ALTER COLUMN created_at TYPE TIMESTAMPTZ;

-- Capsule Contents table
ALTER TABLE capsule_contents ALTER COLUMN added_at TYPE TIMESTAMPTZ;

-- User Verification table
ALTER TABLE user_verification ALTER COLUMN expires_at TYPE TIMESTAMPTZ;

-- Refresh Tokens table
ALTER TABLE refresh_tokens ALTER COLUMN expires_at TYPE TIMESTAMPTZ;
ALTER TABLE refresh_tokens ALTER COLUMN created_at TYPE TIMESTAMPTZ;
