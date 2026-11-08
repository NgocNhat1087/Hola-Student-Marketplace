# Architecture

The application follows the SRS three-tier design:

```text
React + TypeScript frontend
          │ REST / bearer session
Spring Boot modular monolith
  controller → service → repository → JPA entity
          │
MySQL 8 (H2 for tests)
```

Account/profile code is isolated under `com.hsm.account`; security is under `com.hsm.security`; integrations are adapter interfaces under `com.hsm.integration`; unfinished marketplace areas remain skeleton boundaries. Spring Mail and OAuth2 Client dependencies are present so production credentials can be configured without replacing the architecture.
