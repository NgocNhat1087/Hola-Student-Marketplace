# Scope boundary

Complete business implementation is limited to UC.1, UC.2, UC.3, UC.6 and UC.7. Skeleton-only boundaries cover recover/change password, posts, search, chat, comments, favorites, follows, notifications, offers, orders, complaints, reviews, boosts, payment reconciliation and admin operations. The existing SQL preserves their entity foundation without exposing unfinished workflows as working features.

Known blocking requirement: the SRS does not define the seller ranking formula. A product decision is required before ranking can be calculated or displayed as a numeric result.
