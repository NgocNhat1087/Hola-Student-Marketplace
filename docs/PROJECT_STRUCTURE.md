# Hola Student Marketplace – Project Structure

The project uses React/Vite for the frontend and Spring Boot/Java for the backend. The structure below keeps the existing working account and OAuth implementation while providing feature boundaries for the remaining modules.

```text
hola-student-marketplace/
├── frontend/                          # React + Vite client
│   ├── src/
│   │   ├── features/                  # UI grouped by business feature
│   │   │   ├── account/               # Registration, login, profile
│   │   │   ├── post/                  # Post creation, search, detail
│   │   │   ├── trading/               # Offers, orders, complaints, reviews
│   │   │   ├── boost/                 # Post boost and QR payment
│   │   │   ├── chat/                  # Messaging
│   │   │   ├── notification/          # Notifications
│   │   │   └── admin/                 # Approval and report handling
│   │   ├── components/                # Reusable UI components
│   │   ├── layouts/                   # Page layouts
│   │   ├── context/                   # React context providers
│   │   ├── pages/                     # Route-level pages
│   │   ├── services/                  # API client and service tests
│   │   ├── shared/                    # Shared frontend helpers
│   │   ├── main.tsx                   # Frontend entry point
│   │   ├── styles.css
│   │   └── types.ts
│   └── package.json
│
├── backend/                           # Spring Boot server
│   └── src/
│       ├── main/java/com/hsm/
│       │   ├── account/                # User, profile, authentication
│       │   │   ├── controller/
│       │   │   ├── dto/
│       │   │   ├── entity/
│       │   │   ├── repository/
│       │   │   └── service/
│       │   ├── post/                   # Post, PostEdit, Category
│       │   ├── community/              # Comment, Favorite, Follow, Report
│       │   ├── trading/                # Offer, Order, Complaint, Review
│       │   ├── admin/                  # Administration and configuration
│       │   ├── plugins/                # Optional switchable features
│       │   │   ├── boost/
│       │   │   ├── chat/
│       │   │   └── notification/
│       │   ├── integration/            # External service adapters
│       │   │   ├── email/
│       │   │   ├── google/
│       │   │   └── payment/
│       │   ├── shared/                 # Cross-cutting infrastructure
│       │   │   ├── database/
│       │   │   ├── middleware/
│       │   │   └── error/
│       │   ├── config/                 # Spring configuration
│       │   ├── security/               # Session and Google OAuth handlers
│       │   └── HsmApplication.java     # Backend entry point
│       └── main/resources/
│           └── application.yml
│
└── database/
    └── HSM_Database_Design.sql        # MySQL 8 physical schema
```

## Current implementation mapping

- `account` is implemented in `backend/src/main/java/com/hsm/account`.
- Google OAuth handlers remain in `security` because Spring Security discovers them as authentication handlers.
- Email adapters remain in `integration` until the email plugin boundary is implemented.
- The other feature folders are prepared for incremental implementation; no existing routes were moved, so the current application and login flow remain intact.
