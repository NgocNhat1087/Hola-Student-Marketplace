# Requirement traceability

| Requirement | Backend | Frontend | Data | Verification |
|---|---|---|---|---|
| UC.1 registration/OTP | `AuthController`, `AccountService`, `SpringMailEmailService` | React registration form | `users` OTP columns | Spring context/build; service/API tests planned |
| UC.2 password login | `AccountService`, `SessionService`, `SessionFilter` | React login form and token storage | `users`, in-memory active sessions | Spring context/build |
| UC.2 Google OAuth | `/api/auth/google` boundary; Spring OAuth dependency and env config | Login page boundary | `google_subject` | Not externally verifiable without credentials |
| UC.3 logout | `AuthController`, `SessionService` | token state boundary | session invalidation | service/API tests planned |
| UC.6 profile | `ProfileController`, `ProfileImageController`, `AccountService` | profile/edit forms | `user_profiles` | Spring context/build |
| UC.7 profile view | `ProfileController`, `ReviewRepository` | profile page | `reviews`, `user_profiles` | Spring context/build |
| SRS image rule | content type, size and `ImageIO` validation | upload-ready API boundary | profile URLs | targeted upload tests required |

Technical decisions are the bearer token session implementation, H2 test default, BCrypt, local upload path for development and null seller ranking. These are not SRS business rules.
