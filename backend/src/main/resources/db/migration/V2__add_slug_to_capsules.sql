-- V2__add_slug_to_capsules.sql

ALTER TABLE capsules
    ADD COLUMN slug VARCHAR(100) NOT NULL DEFAULT '';

UPDATE capsules
SET slug = LOWER(
                   REGEXP_REPLACE(
                           REGEXP_REPLACE(title, '[^a-zA-Z0-9\s-]', '', 'g'),
                           '\s+', '-', 'g'
                   )
           ) || '-' || LEFT(id::TEXT, 6)
WHERE slug = '';

ALTER TABLE capsules
    ALTER COLUMN slug DROP DEFAULT;

ALTER TABLE capsules
    ADD CONSTRAINT uq_capsules_slug UNIQUE (slug);

CREATE INDEX idx_capsules_slug ON capsules (slug);