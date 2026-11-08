# Use-case catalog

| ID | Name | Scope |
|---|---|---|
| UC.1 | Register an account | Fully implemented |
| UC.2 | Log in | Fully implemented; Google boundary/configuration documented |
| UC.3 | Log out | Fully implemented |
| UC.4–UC.5 | Recover/change password | Skeleton-only |
| UC.6 | Manage profile | Fully implemented, including text and image API foundations |
| UC.7 | View profile | Fully implemented with derived rating and unresolved rank |
| UC.8–UC.41 | Posts, discovery, community, trading, boost, administration | Skeleton-only |

UC.1 precedes UC.2 because email verification is required for password login. UC.2 establishes the session required by UC.3, UC.6 and UC.7. UC.7 reads the profile and review model but does not implement the out-of-scope review workflow.
