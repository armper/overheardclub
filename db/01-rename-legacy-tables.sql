-- Step 1 of migrating an existing Overheard Club (JHipster) PostgreSQL database.
-- Run this BEFORE starting the new app. It moves the old tables out of the way so
-- Flyway can create the new schema alongside them:
--   psql "$DATABASE_URL" -f db/01-rename-legacy-tables.sql

BEGIN;
ALTER TABLE jhi_user            RENAME TO legacy_user;
ALTER TABLE jhi_user_authority  RENAME TO legacy_user_authority;
ALTER TABLE topic               RENAME TO legacy_topic;
ALTER TABLE post                RENAME TO legacy_post;
ALTER TABLE overheard_comment   RENAME TO legacy_comment;
ALTER TABLE post_user_uprank    RENAME TO legacy_post_user_uprank;
-- Liquibase bookkeeping from the old app is no longer needed.
DROP TABLE IF EXISTS databasechangelog;
DROP TABLE IF EXISTS databasechangeloglock;
COMMIT;
