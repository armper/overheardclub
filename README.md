# Overheard Club

A small, members-only feed for the funny, strange and brilliant things people say in passing.
Members post a quote under a topic, leave one emoji reaction per post, and comment.

- **Backend**: Java 21, Spring Boot 3, Spring Security (JWT), Spring Data JPA, Flyway. H2 in development, PostgreSQL in production.
- **Frontend**: React 18, TypeScript, Vite, React Router. No UI framework; one small stylesheet with a minimal monochrome design.

## What's in the app

| Route | What it does |
|---|---|
| `/` | Feed: **Top this week** or **Latest**, with load-more. Visitors see a short landing section above it. |
| `/topics` | Topic grid with post counts; members can create topics inline. |
| `/topics/:id` | A topic's posts. The creator or an admin can rename it, or delete it while it's empty. |
| `/posts/:id` | The full quote, reactions, and comments. The author or an admin can edit or delete. |
| `/posts/new`, `/posts/:id/edit` | Post editor, with `?topic=<id>` to pre-select a topic. |
| `/login`, `/register` | Sign in or join. Registration is immediate (no email step). |
| `/settings` | Display name, email, and password. |
| `/admin/users` | Admins: member list and role changes. |

Reading is public; posting, reacting and commenting need an account. Each member gets one reaction per post: 👍 ❤️ 😂 🤯 🙄. Picking another emoji switches it, clicking the same one removes it.

## Run it locally

Prerequisites: Java 21 and Node 20+.

```bash
# Backend on :8076 (H2 file database in ./data, sample data seeded on first run)
./mvnw spring-boot:run

# Frontend dev server on :5173, proxying /api to :8076
cd frontend && npm install && npm run dev
```

Dev accounts (created on first start of an empty dev database): `admin` / `admin`, `alice` / `password`, `sam` / `password`.

To serve the built frontend from the backend instead of the dev server:

```bash
cd frontend && npm run build   # writes to src/main/resources/static
./mvnw spring-boot:run          # open http://localhost:8076
```

## Tests

```bash
./mvnw test                      # API flow test against in-memory H2
cd frontend && npm run typecheck
```

## Production build and deploy

```bash
./mvnw -Pprod package            # builds the React app and bundles it into target/overheardclub-1.0.0.jar
SPRING_PROFILES_ACTIVE=prod java -jar target/overheardclub-1.0.0.jar
```

Configuration is by environment variable:

| Variable | Purpose |
|---|---|
| `DATABASE_URL` | JDBC URL, e.g. `jdbc:postgresql://host:5432/overheardclub` |
| `DATABASE_USER`, `DATABASE_PASSWORD` | Database credentials |
| `APP_JWT_SECRET` | Token signing secret, at least 32 characters. Required in production. |
| `APP_SEED_ADMIN_PASSWORD` | Creates the `admin` account on first start of an empty database. |
| `PORT` | HTTP port (defaults to 8076) |

`src/main/appengine/app.yaml` is a starting point for Google App Engine; supply the variables above through the console or a secret manager rather than committing them.

## Migrating data from the previous (JHipster) version

The old database can be carried over. See the two scripts in `db/`:

1. `db/01-rename-legacy-tables.sql` before starting the new app.
2. `db/02-copy-legacy-data.sql` after its first start, before anyone registers.

Members keep their passwords. Old reactions are migrated as 👍 because the old schema didn't record which emoji each member chose.
