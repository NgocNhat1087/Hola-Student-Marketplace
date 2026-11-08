# HSM: Integration of latest Git clone with UC34–UC39

## Merge policy
- Base is the uploaded `Hola-Student-Marketplace(1).zip` (latest team's Git clone).
- Preserve teammate's authentication updates, Google OAuth callbacks, password reset pages/service, session management, existing React design, SQL schema, and environment sample.
- Add the six Admin controllers, services, JPA repositories, mapped entities, DTOs and support classes from prior Entity Mapping Fixed revision.
- Add Admin React page/API and route `http://localhost:5173/admin`; show nav to users with ADMIN role.
- Fix Java interface compile error in `UserManagementRepository.searchUsers` by using `default`.
- Change Java target 25 to 17 in `backend/pom.xml` to match installed JDK 17; source syntax supports Java 17.
- Correct logout UI navigation to `/` instead of reporting an OAuth error on normal logout.
- Keep `JPA_DDL_AUTO=validate` for safety; no live database was modified.

## How to run (Windows)
1. Apply the schema only on a new disposable test database. **Schema contains DROP DATABASE**; do not import over real data.
2. Create `.env` from `.env.example`, set MySQL credentials, `EMAIL_ENABLED=false` if SMTP is not configured. Never commit the `.env` file.
3. `cd backend && mvn clean compile && mvn spring-boot:run`
4. `cd frontend && npm install && npm run dev`
5. Log in with an actual Admin user whose BCrypt hash matches the password you enter and whose `email_verified_at` is set. Then open `/admin`.

## Troubleshooting previous login 500
- The login JSON `INTERNAL_ERROR` is returned by the global exception handler for any unhandled exception. It does not prove the password is wrong.
- Check the IntelliJ backend error logs immediately after POST `/api/auth/login` for the specific exception.
- Confirm DB user record: `SELECT user_id, email, account_role, account_status, email_verified_at, password_hash FROM users WHERE email='admin@hsm.local';` (do not paste or share hashes publicly).
- A seed user can only log in if the seed was imported and its BCrypt hash matches the plaintext test password. Never replace bcrypt with plaintext.
- Some controllers expect a Bearer session token returned from login.

## Important testing limits
- No Maven binary, Maven dependencies, or MySQL server are available in this execution environment. Spring Boot compilation and full integration tests have **not** been executed here.
- Admin data/persistence is still partially implemented via native SQL in an EntityManager-backed JPA repository fragment. This is not 100% derived JPA query methods.
- Many trading/boost user-side flows are not implemented in this early-stage project. Admin endpoints may require representative seeded relational data.
- Compare the existing MySQL schema and Entity read-model columns under `admin/entity/` if Hibernate validation reveals additional mismatches.
