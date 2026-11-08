# Implemented use cases

UC19 comments: GET/POST /api/marketplace/posts/{postId}/comments; PUT/DELETE /api/marketplace/comments/{id}. Only the author can edit/delete; delete is soft delete.

UC20 favorites: GET /api/marketplace/favorites; POST/DELETE /api/marketplace/favorites/{postId}.

UC21 following: GET /api/marketplace/following; POST/DELETE /api/marketplace/following/{userId}; GET /api/marketplace/users/{userId}/posts.

UC22 in-app notifications: GET /api/marketplace/notifications; PUT /api/marketplace/notifications/{id}/read; PUT /api/marketplace/notifications/read-all. These endpoints display and acknowledge notifications already stored in the database. Email delivery and automatic notification generation are NOT implemented.

UC23 reports: POST /api/marketplace/posts/{postId}/reports with {"reason":"..."}; GET /api/marketplace/reports/mine. A user can report a post once.

All write operations require the existing Bearer session token. UI: /marketplace (navigation link). Uses existing database tables from database/HSM_Database_Design.sql. You must import/create the database schema first and supply valid existing post IDs and user IDs. The project still requires the original .env settings and a running MySQL instance.

Run backend: cd backend && mvn spring-boot:run (JDK 25 as configured in pom.xml)
Run frontend: cd frontend && npm ci && npm run dev

NOTE: Build and integration tests could not be completed in this environment. The frontend uses the existing authentication system. This is a functional initial implementation, not a verified production release.
