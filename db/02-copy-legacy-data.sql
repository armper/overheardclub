-- Step 2 of migrating an existing database. Run this AFTER the new app has
-- started once (Flyway has created app_user, topic, post, comment, reaction)
-- and BEFORE anyone registers:
--   psql "$DATABASE_URL" -f db/02-copy-legacy-data.sql
--
-- Notes
-- * Password hashes are BCrypt in both schemas, so members keep their passwords.
-- * The old schema stored per-post counters plus the set of members who had
--   reacted, but not which reaction each member chose. Each old reaction is
--   migrated as NICE so "one reaction per member" still holds; the old
--   per-emoji counts are not recoverable.
-- * Only activated users are migrated. Users without an email get a
--   placeholder address they can change in Settings.

BEGIN;

INSERT INTO app_user (id, username, email, password_hash, display_name, role, created_at)
SELECT u.id,
       u.login,
       COALESCE(NULLIF(u.email, ''), u.login || '@migrated.invalid'),
       u.password_hash,
       COALESCE(NULLIF(TRIM(COALESCE(u.first_name, '') || ' ' || COALESCE(u.last_name, '')), ''), u.login),
       CASE WHEN EXISTS (SELECT 1 FROM legacy_user_authority a
                         WHERE a.user_id = u.id AND a.authority_name = 'ROLE_ADMIN')
            THEN 'ADMIN' ELSE 'USER' END,
       COALESCE(u.created_date, NOW())
FROM legacy_user u
WHERE u.activated = TRUE;

INSERT INTO topic (id, title, created_by_id, created_at)
SELECT t.id, t.title, t.user_id, NOW()
FROM legacy_topic t
WHERE EXISTS (SELECT 1 FROM app_user u WHERE u.id = t.user_id);

INSERT INTO post (id, title, content, author_id, topic_id, created_at)
SELECT p.id, p.title, p.content, p.user_id, p.topic_id, p.date
FROM legacy_post p
WHERE EXISTS (SELECT 1 FROM app_user u WHERE u.id = p.user_id)
  AND EXISTS (SELECT 1 FROM topic t WHERE t.id = p.topic_id);

INSERT INTO comment (id, content, post_id, author_id, created_at)
SELECT c.id, c.content, c.post_id, c.user_id, c.date
FROM legacy_comment c
WHERE EXISTS (SELECT 1 FROM post p WHERE p.id = c.post_id)
  AND EXISTS (SELECT 1 FROM app_user u WHERE u.id = c.user_id);

INSERT INTO reaction (post_id, user_id, type, created_at)
SELECT pu.post_id, pu.user_uprank_id, 'NICE', NOW()
FROM legacy_post_user_uprank pu
WHERE EXISTS (SELECT 1 FROM post p WHERE p.id = pu.post_id)
  AND EXISTS (SELECT 1 FROM app_user u WHERE u.id = pu.user_uprank_id)
ON CONFLICT (post_id, user_id) DO NOTHING;

-- Move the identity sequences past the migrated ids.
SELECT setval(pg_get_serial_sequence('app_user', 'id'), COALESCE((SELECT MAX(id) FROM app_user), 0) + 1, false);
SELECT setval(pg_get_serial_sequence('topic', 'id'),    COALESCE((SELECT MAX(id) FROM topic), 0) + 1, false);
SELECT setval(pg_get_serial_sequence('post', 'id'),     COALESCE((SELECT MAX(id) FROM post), 0) + 1, false);
SELECT setval(pg_get_serial_sequence('comment', 'id'),  COALESCE((SELECT MAX(id) FROM comment), 0) + 1, false);

COMMIT;

-- Once you're happy with the result:
-- DROP TABLE legacy_post_user_uprank, legacy_comment, legacy_post, legacy_topic, legacy_user_authority, legacy_user;
