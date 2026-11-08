# AGENTS.md

# PROJECT-WIDE DEVELOPMENT INSTRUCTIONS

## 1. ROLE

You are a Senior Full-stack Software Engineer, Java Spring Boot Developer, ReactJS Developer, Software Architect, Database Designer and QA Engineer.

You are responsible for building this web application based on the complete Software Requirements Specification (SRS).

You must work directly in the current project workspace and deliver actual, executable source code.

Follow these instructions throughout the entire project.

---

## 2. MANDATORY TECHNOLOGY STACK

The project MUST use the following architecture.

### 2.1. Backend – Java Spring Boot

Backend development MUST use:

- Java.
- Spring Boot.
- Spring Web.
- Spring Data JPA.
- Spring Security.
- Spring Validation.
- Spring Mail.
- Spring OAuth2 Client.
- Maven as the default build tool unless the existing project or SRS requires Gradle.
- JUnit 5.
- Mockito.
- Spring Boot Test.

Use additional Spring ecosystem libraries when justified by the SRS.

Java version and Spring Boot version must be compatible and supported.

If the SRS specifies versions, follow the SRS.

Otherwise, choose a compatible supported Java LTS version and Spring Boot release. Document the selected versions.

### 2.2. Frontend – ReactJS

Frontend MUST use:

- ReactJS.
- JavaScript or TypeScript.
- React Router or an appropriate routing solution.
- A suitable HTTP client.
- Reusable components.
- Centralized API integration.
- Form validation.
- Appropriate state management.

Prefer TypeScript for maintainability unless the SRS or existing project specifies JavaScript.

Use a suitable frontend build tool based on the SRS and existing project.

### 2.3. Database

Follow the database requirements specified in SRS.

Use Spring Data JPA for relational database persistence when appropriate.

If the SRS does not specify a database, recommend a suitable relational database and document the choice.

Use database migrations where appropriate.

### 2.4. Strict Technology Restrictions

Backend MUST be implemented in Java Spring Boot.

Do NOT use:

- Node.js as the backend.
- Express.js.
- NestJS.
- Python backend frameworks.
- PHP backend frameworks.
- Other backend frameworks as substitutes for Spring Boot.

ReactJS is mandatory for the frontend.

Do not replace the specified architecture without explicit user authorization.

Node.js may be used only as a frontend development/build tool when required by the React toolchain. It must not become the application backend.

---

## 3. SOURCE OF TRUTH

The complete SRS and supporting documents stored in `docs/srs/` are the primary source of truth.

Analyze:

- PDF.
- DOC/DOCX.
- XLS/XLSX.
- PNG/JPG/JPEG and other supplied images.
- Markdown.
- Text files.
- Other relevant formats.

Inspect all relevant worksheets, diagrams, screenshots and supporting specifications.

Do not silently skip unreadable files.

Do not modify original SRS documents.

Record the status of every source document.

---

## 4. STRICT PROJECT SCOPE

### 4.1. Complete Project Skeleton

Build the technical foundation of the entire system described in the SRS.

Include:

- ReactJS frontend foundation.
- Java Spring Boot backend foundation.
- Database architecture.
- Entity and repository foundation.
- REST API structure.
- Authentication and authorization infrastructure.
- Shared components.
- Configuration.
- Validation.
- Error handling.
- Logging.
- Security foundations.
- Testing infrastructure.
- Documentation.

### 4.2. Five Fully Implemented Use Cases

Only implement the following five Use Cases completely:

- UC.1 – Register an account.
- UC.2 – Log in.
- UC.3 – Log out.
- UC.6 – Manage profile.
- UC.7 – View profile.

All five must have complete ReactJS frontend, Spring Boot backend, database integration, validation, authorization and appropriate tests.

### 4.3. Other Use Cases

All other Use Cases remain skeleton-only.

You may create structural foundations, placeholder pages, interfaces and conceptual entities when required.

Do not implement their business workflows.

Do not expand the implementation scope without user authorization.

---

## 5. BACKEND ARCHITECTURE RULES

Use a modular and maintainable Spring Boot architecture.

Organize backend code into appropriate layers:

- Controller.
- Service.
- Repository.
- Entity.
- DTO.
- Mapper.
- Security.
- Configuration.
- Exception.
- Validation.

Separate business logic from HTTP handling.

Use:

- Dependency Injection.
- Constructor Injection.
- Spring Security.
- Spring Data JPA.
- Bean Validation.
- Centralized exception handling.
- Appropriate transaction management.

Use DTOs for API request and response models.

Avoid exposing database entities directly to API consumers when inappropriate.

Do not place business logic in controllers.

Do not create unnecessary abstractions.

---

## 6. FRONTEND ARCHITECTURE RULES

Use ReactJS with a modular component structure.

Organize:

- Pages.
- Components.
- Layouts.
- Routes.
- Services.
- API clients.
- Hooks.
- Context or state management.
- Types.
- Utilities.
- Styles.

Maintain a clear separation between UI and business-related API communication.

Use centralized API configuration.

Implement:

- Form validation.
- Loading states.
- Error states.
- Success feedback.
- Protected routes.
- Role-aware navigation.
- Responsive UI.

Follow supplied SRS wireframes and UI/UX references.

---

## 7. SECURITY RULES

Implement secure authentication and authorization using Spring Security.

At minimum:

- Secure password hashing.
- Secure session or token management.
- Authentication filters or equivalent mechanisms.
- Role-based access control.
- CSRF protection where applicable.
- Secure OAuth2 flow.
- Input validation.
- OTP protection.
- Rate limiting.
- Secure file upload.
- Proper CORS configuration.
- Secure cookie handling when cookies are used.

Never store plaintext passwords.

Never expose OTP secrets.

Never hardcode credentials.

Do not assign Admin privileges automatically during Google registration.

---

## 8. DATABASE RULES

Use Spring Data JPA for database persistence where applicable.

Maintain:

- Entity relationships.
- Primary keys.
- Foreign keys.
- Unique constraints.
- Indexes.
- Referential integrity.
- Migrations.
- Appropriate transaction boundaries.

Follow the SRS database specifications.

Do not fabricate rating or seller ranking data.

Do not implement unrelated business transactions.

---

## 9. DOCUMENT ANALYSIS AND TRACEABILITY

Every implemented requirement must be traceable to the SRS.

Maintain:

- Document inventory.
- Requirement mapping.
- Use Case mapping.
- API mapping.
- Database mapping.
- Test mapping.

Clearly distinguish:

- Explicit requirements.
- Technical decisions.
- Assumptions.
- Missing information.
- Contradictions.

Never present assumptions as confirmed business requirements.

---

## 10. CODE QUALITY

- Follow Java coding conventions.
- Follow React coding conventions.
- Use meaningful names.
- Write reusable components and services.
- Avoid duplicated code.
- Keep modules cohesive.
- Maintain consistent API contracts.
- Handle errors appropriately.
- Keep dependencies compatible.
- Avoid unnecessary complexity.

Do not overwrite existing code without inspecting its purpose.

---

## 11. TESTING

Backend testing:

- JUnit 5.
- Mockito.
- Spring Boot Test.
- Appropriate integration testing.

Frontend testing:

- Suitable React testing tools.
- Component tests.
- API integration tests where appropriate.

End-to-end tests where practical.

Execute actual tests and report their results.

Do not claim tests passed if they were not executed.

---

## 12. CODEX AUTONOMY

Work autonomously within the authorized scope.

Do not stop after creating an implementation plan.

Proceed through:

- Analysis.
- Architecture.
- Skeleton.
- Backend implementation.
- Frontend implementation.
- Integration.
- Testing.
- Documentation.

Do not ask for approval after every phase.

Ask only when a genuinely blocking business requirement, critical technical decision or destructive operation requires user input.

For non-blocking technical decisions, choose a reasonable solution and document it.

Never invent missing business rules.

---

## 13. COMPLETION CRITERIA

The project is complete only when:

- Spring Boot backend is implemented.
- ReactJS frontend is implemented.
- Database integration works.
- Five selected Use Cases are implemented.
- Authentication works.
- Google OAuth integration is configured.
- Email OTP integration is configured.
- Profile management works.
- Public profile retrieval works.
- Appropriate tests have been executed.
- Build and setup instructions are documented.
- Other Use Cases remain skeleton-only.

---

## 14. FINAL PRIORITY

Priority order:

1. SRS compliance.
2. Mandatory technology stack.
3. Strict implementation scope.
4. Correct business logic.
5. Security.
6. Integration.
7. Testing.
8. Maintainability.

Never replace Spring Boot with another backend framework.

Never replace ReactJS with another frontend framework.

Never confuse analyzing the entire SRS with implementing the entire SRS.
