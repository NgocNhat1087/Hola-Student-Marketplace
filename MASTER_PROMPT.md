# MASTER PROMPT

# FULL-STACK WEB APPLICATION DEVELOPMENT

# JAVA SPRING BOOT + REACTJS

## 1. PROJECT MISSION

Develop a web application based on all SRS documents in:

`docs/srs/`

The project must use the following mandatory architecture:

BACKEND:
Java + Spring Boot.

FRONTEND:
ReactJS.

DATABASE:
Follow SRS. If unspecified, recommend a suitable relational database.

The project has two implementation objectives:

OBJECTIVE A:
Build the complete technical skeleton of the entire system described in the SRS.

OBJECTIVE B:
Fully implement exactly five Use Cases:

- UC.1 – Register an account.
- UC.2 – Log in.
- UC.3 – Log out.
- UC.6 – Manage profile.
- UC.7 – View profile.

Read and understand the entire SRS, but do not implement other Use Cases beyond skeleton level.

Work directly in the current workspace.

Follow AGENTS.md at all times.

---

## 2. MANDATORY TECHNOLOGY STACK

### Backend

Use:

- Java.
- Spring Boot.
- Spring Web.
- Spring Data JPA.
- Spring Security.
- Spring Validation.
- Spring Mail.
- Spring OAuth2 Client.
- Maven.
- JUnit 5.
- Mockito.
- Spring Boot Test.

Choose compatible versions based on SRS and supported releases.

Use an appropriate relational database according to SRS.

### Frontend

Use:

- ReactJS.
- TypeScript preferred unless SRS specifies JavaScript.
- React Router.
- Appropriate HTTP client.
- Reusable React components.
- Appropriate state management.
- Responsive CSS framework or styling solution according to SRS.

### Strict Restrictions

Do not implement backend using Node.js, Express.js, NestJS, Python or PHP.

Do not replace Spring Boot with any other backend framework.

Do not replace ReactJS with another frontend framework.

Node.js may be used only as a frontend build/development runtime if needed.

---

## 3. DOCUMENT ANALYSIS

Inspect all documents in `docs/srs/`.

Supported source formats include:

- PDF.
- DOC/DOCX.
- XLS/XLSX.
- PNG/JPG/JPEG.
- Markdown.
- TXT.
- Other available formats.

For each document:

1. Identify its purpose.
2. Extract requirements.
3. Inspect relevant tables and diagrams.
4. Identify related Use Cases.
5. Record dependencies.
6. Identify conflicts or missing information.
7. Record whether the file was successfully analyzed.

For spreadsheets, inspect relevant worksheets and data relationships.

For diagrams and images, inspect visual content.

Do not claim a document was analyzed if it could not be read.

Create:

- `docs/analysis/DOCUMENT_INVENTORY.md`
- `docs/analysis/SRS_ANALYSIS.md`
- `docs/analysis/USE_CASE_CATALOG.md`
- `docs/analysis/REQUIREMENT_TRACEABILITY.md`
- `docs/analysis/SCOPE_BOUNDARY.md`
- `docs/analysis/IMPLEMENTATION_PLAN.md`

---

## 4. PROJECT SKELETON

Build the foundation of the complete system.

Use an appropriate project structure.

Suggested structure:

PROJECT_ROOT/

- AGENTS.md
- MASTER_PROMPT.md
- README.md
- .env.example
- .gitignore
- docs/
- backend/
- frontend/
- database/
- tests/

### Backend structure

Organize the Spring Boot application into appropriate packages.

Suggested organization:

- config/
- security/
- controller/
- service/
- repository/
- entity/
- dto/
- mapper/
- exception/
- validation/

Use a modular package structure if the SRS justifies it.

### Frontend structure

Suggested organization:

- src/
  - assets/
  - components/
  - layouts/
  - pages/
  - routes/
  - services/
  - hooks/
  - contexts/
  - types/
  - utils/
  - styles/

Adapt the structure based on the selected frontend architecture.

### Skeleton-only modules

Create appropriate foundations for the remaining Use Cases without implementing their business logic.

---

## 5. UC.1 – REGISTER AN ACCOUNT

Actor: Guest.

Implement using ReactJS and Spring Boot.

Requirements:

- Registration page.
- Registration API.
- Email validation.
- Password validation based on SRS.
- Duplicate email checking.
- Account creation.
- Password hashing.
- Six-digit OTP generation.
- Email delivery through Spring Mail or an appropriate email provider.
- OTP verification API.
- OTP resend API.
- OTP expiration.
- OTP attempt limits.
- Rate limiting.
- Account verification status.

Security:

- Never expose OTP in API responses.
- Never log OTP values.
- Store OTP securely.
- Prevent brute-force attempts.
- Handle repeated requests safely.
- Ensure account activation follows SRS rules.

Implement database persistence using Spring Data JPA.

Write backend and frontend tests.

---

## 6. UC.2 – LOG IN

Actors: Guest, Admin.

Implement two authentication methods.

### Email and Password

- ReactJS login page.
- Spring Boot login endpoint.
- Credential validation.
- Secure authentication.
- Session or token creation.
- User role retrieval.
- Authentication error handling.
- Protected routes.

### Google OAuth

Use Spring Security OAuth2 Client.

Implement:

- Google OAuth authorization.
- Callback handling.
- Identity validation.
- First-time account creation.
- Existing Google account authentication.
- Safe account linking where applicable.
- OAuth error handling.
- Appropriate role assignment.

Do not automatically assign Admin role to new accounts.

Follow the SRS for user role and account activation rules.

Use environment variables for Google OAuth credentials.

---

## 7. UC.3 – LOG OUT

Actors: Buyer, Seller, Admin.

Implement:

- Logout action in ReactJS.
- Backend logout handling where required.
- Session termination.
- Token invalidation or revocation where applicable.
- Secure credential cleanup.
- Authentication state reset.
- Guest redirection.
- Protected route enforcement.

Test that terminated sessions cannot be reused.

---

## 8. UC.6 – MANAGE PROFILE

Actors: Buyer, Seller.

Implement a complete profile management workflow.

### Frontend

- Profile management page.
- Avatar upload.
- Cover photo upload.
- Bio editing.
- Nickname editing.
- Image preview.
- Form validation.
- Loading and error states.
- Success notifications.

### Backend

Use Spring Boot to implement:

- Profile retrieval.
- Profile update.
- Avatar upload.
- Cover photo upload.
- Image storage integration.
- File validation.
- Database persistence.
- Authorization.

Security:

- Only authenticated users can manage profiles.
- Users may only modify their own profiles.
- Validate file content and size.
- Prevent unsafe uploads.
- Handle image replacement and failures safely.

Follow all field constraints and business rules in SRS.

---

## 9. UC.7 – VIEW PROFILE

Actors: Buyer, Seller.

Implement a public profile viewing workflow.

### Frontend

Create a profile page displaying:

- Avatar.
- Cover photo.
- Nickname.
- Bio.
- Average rating.
- Seller ranking.

### Backend

Use Spring Boot REST APIs to retrieve profile information.

Use actual database data.

Retrieve average rating from the rating-related data model.

Retrieve seller ranking according to SRS.

Handle:

- Nonexistent profiles.
- Missing images.
- Empty bio.
- Users without ratings.
- Sellers without rankings.
- Data visibility rules.

CRITICAL:

Do not invent rating calculation rules.

Do not invent seller ranking formulas.

Do not use hardcoded rating values or fake seller rankings.

If formulas are missing from SRS, identify the missing rules and ask for clarification before implementing those calculations.

---

## 10. DATABASE DESIGN

Design the database based on the complete SRS.

Use JPA entities and repositories.

Potential data areas:

- User.
- Role.
- User profile.
- Email verification.
- OTP.
- OAuth identity.
- Rating.
- Seller ranking.

Determine the actual entities and relationships from SRS.

Create:

- ERD.
- Entity definitions.
- Database schema.
- Migration scripts.
- Indexes.
- Constraints.
- Relationship documentation.

Avoid unnecessary tables and duplication.

Do not implement unrelated transaction workflows.

---

## 11. FRONTEND AND BACKEND INTEGRATION

Integrate ReactJS with Spring Boot REST APIs.

Define:

- API base URL.
- Request and response formats.
- Authentication strategy.
- Error response format.
- CORS policy.
- Session or token handling.
- Protected route behavior.

Ensure that frontend components use actual backend endpoints.

Do not use mock data as a substitute for required functionality.

Mocking is allowed only in isolated tests.

---

## 12. TESTING

### Backend

Use:

- JUnit 5.
- Mockito.
- Spring Boot Test.
- MockMvc or appropriate integration testing tools.

### Frontend

Use appropriate React testing tools.

Test all five Use Cases.

Minimum test coverage scenarios:

UC.1:

- Successful registration.
- Duplicate email.
- Invalid email.
- Invalid password.
- Correct OTP.
- Incorrect OTP.
- Expired OTP.
- Resend OTP.
- OTP rate limiting.

UC.2:

- Successful email/password login.
- Incorrect credentials.
- Google OAuth success.
- First Google login.
- Existing Google account.
- Role assignment.

UC.3:

- Successful logout.
- Session termination.
- Invalidated session access.

UC.6:

- Successful profile update.
- Invalid input.
- Invalid image.
- Unauthorized update.
- Data persistence.

UC.7:

- Successful profile retrieval.
- Nonexistent profile.
- Missing ratings.
- Missing ranking.
- Correct rating and ranking retrieval.

Execute the tests and report real results.

---

## 13. EXTERNAL SERVICES

Identify all external integrations required.

Potential services:

- Email provider.
- Google OAuth.
- Image storage.
- Database.

Use secure environment variables.

Create `.env.example`.

Document configuration.

If credentials are unavailable, implement the integration code and clearly document what remains to be configured or verified.

Do not claim successful external integration without actually testing it.

---

## 14. IMPLEMENTATION ORDER

Execute the following phases autonomously.

PHASE 1:
Inspect workspace and SRS documents.

PHASE 2:
Analyze requirements and define project scope.

PHASE 3:
Select compatible technology versions and design architecture.

PHASE 4:
Create Spring Boot backend skeleton.

PHASE 5:
Create ReactJS frontend skeleton.

PHASE 6:
Configure database and common infrastructure.

PHASE 7:
Implement UC.1.

PHASE 8:
Implement UC.2.

PHASE 9:
Implement UC.3.

PHASE 10:
Implement UC.6.

PHASE 11:
Implement UC.7.

PHASE 12:
Integrate frontend, backend and database.

PHASE 13:
Run tests and fix issues.

PHASE 14:
Complete documentation and final verification.

Do not stop after planning.

Do not ask for approval after every phase.

Continue autonomously unless a genuinely blocking decision requires user input.

---

## 15. DOCUMENTATION

Maintain:

- README.md.
- docs/analysis/.
- docs/architecture/.
- docs/database/.
- docs/api/.
- docs/testing/.
- docs/analysis/IMPLEMENTATION_STATUS.md.

Document:

- Technology versions.
- Installation commands.
- Environment variables.
- Backend startup.
- Frontend startup.
- Database setup.
- API endpoints.
- Test execution.
- External service configuration.
- Skeleton-only modules.
- Known limitations.

---

## 16. FINAL DELIVERABLES

Deliver:

1. Complete Spring Boot backend skeleton.
2. Complete ReactJS frontend skeleton.
3. Database schema and migrations.
4. Fully implemented UC.1.
5. Fully implemented UC.2.
6. Fully implemented UC.3.
7. Fully implemented UC.6.
8. Fully implemented UC.7.
9. Integrated REST APIs.
10. Authentication and authorization.
11. Email OTP integration.
12. Google OAuth integration.
13. Profile image upload.
14. Automated tests.
15. API documentation.
16. Setup instructions.
17. Implementation status report.
18. List of skeleton-only modules.

---

## 17. FINAL EXECUTION INSTRUCTION

Read AGENTS.md first.

Inspect the workspace.

Analyze all SRS documents.

Use Java Spring Boot for the backend and ReactJS for the frontend without exception.

Build the complete project skeleton.

Fully implement exactly UC.1, UC.2, UC.3, UC.6 and UC.7.

Test the implementation.

Fix errors.

Complete the documentation.

Work directly in the workspace and deliver executable source code.

Never substitute the required technologies.

Never implement out-of-scope Use Cases.

Never fabricate missing business rules.

The final deliverable must be a working and maintainable web application foundation, not merely a plan, mockup or collection of code examples.
