# SRS analysis

Hola Student Marketplace (HSM) is a student second-hand marketplace with Guest, Buyer, Seller and Admin roles. The SRS describes account management, posts, discovery, community, trading, boost payments and administration. The architecture specified by the SRS is a three-tier client/server application with a feature-based modular monolith backend, one shared MySQL database, optional plugin boundaries for boost/chat/notifications, and adapters for email, Google OAuth and payment gateway services.

Relevant explicit rules for the selected scope:

- UC.1: unique email; password has at least 8 characters with letters and digits; six-digit OTP; OTP valid 5 minutes; resend no more than once per 60 seconds; after more than 5 wrong entries a new code is required; Admin accounts are issued separately.
- UC.2: email/password and Google login; first Google login creates a verified ordinary user; five consecutive failed logins lock login for 15 minutes; an unverified email cannot use password login; Admin is never assigned automatically.
- UC.3: session terminates and user returns to Guest; security requirements specify 60-minute inactivity expiry and immediate logout.
- UC.6: nickname required; avatar and cover images must be JPG or PNG and at most 5 MB; partial updates preserve unchanged fields; failed upload must preserve old data.
- UC.7: authenticated Buyer/Seller views a profile; avatar, cover, nickname, bio, average rating and seller ranking are displayed; no reviews displays “No rating yet”. Average rating and ranking derive from reviews received after completed orders.

Nonfunctional requirements relevant to this foundation include HTTPS/TLS 1.2+, no public email exposure, secure hashes, secure uploads, validation, rate limiting, daily backups and the stated response-time targets. Production deployment still needs TLS termination, a real mail provider, Google client credentials and object storage.

The SRS references BR.30 for rating/ranking but does not state the ranking formula, tie-breaking, or whether ranking is global or filtered. The implementation therefore calculates the explicit average from completed reviews and returns a null seller rank; it does not invent a ranking formula.
