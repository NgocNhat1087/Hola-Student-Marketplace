# Database foundation

The authoritative MySQL 8 physical design is [HSM_Database_Design.sql](../HSM_Database_Design.sql). It contains the complete SRS entity foundation, constraints, indexes, seed settings and skeleton-only modules. Spring Boot uses JPA for the selected use cases and defaults to H2 for local tests; configure MySQL through `.env` for deployment. The JPA tables used by UC.1/2/3/6/7 are `users`, `user_profiles` and `reviews`.
