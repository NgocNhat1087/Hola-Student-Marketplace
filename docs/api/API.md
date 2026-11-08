# API contract

Base URL: `http://localhost:8080`.

- `POST /api/auth/register` — `{email,password}`; creates an unverified user and sends an OTP. Returns 201 without the OTP.
- `POST /api/auth/verify-email` — `{email,otp}`; verifies a valid unused code.
- `POST /api/auth/resend-otp` — `{email}`; enforces the 60-second resend interval.
- `POST /api/auth/login` — `{email,password}`; returns user data and a bearer token. Password login requires verified email.
- `GET /oauth2/authorization/google` — Spring Security OAuth2 authorization entry point when Google client credentials are configured; first login creates/links a verified ordinary user.
- `POST /api/auth/logout` — invalidates the bearer token.
- `GET /api/profile/me` and `GET /api/profile/{id}` — authenticated profile retrieval.
- `PUT /api/profile/me` — `{nickname,bio}`; authenticated owner update.
- `POST /api/profile/me/image` — multipart `kind=avatar|cover`, part `file`; valid JPG/PNG up to 5 MB.

Errors use `{error,message}` and appropriate HTTP status codes. Tokens must be supplied as `Authorization: Bearer <token>`.
