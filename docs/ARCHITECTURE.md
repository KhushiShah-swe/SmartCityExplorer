# Architecture and design decisions

## Frontend

React Router provides home, discovery, saved, details, and not-found routes. `CatalogProvider` loads the catalog once per application mount. A six-second request timeout or invalid response switches to an explicitly labeled bundled demo collection, with a retry action. Requests are cancelled on unmount and stale requests cannot overwrite the current state.

The current small catalog is downloaded in one request. URL query parameters drive client-side filtering, making discovery views shareable and keeping interaction immediate. The API independently supports equivalent query filters for other consumers. For larger catalogs, move the frontend to server-side filtering with pagination.

`FavoritesProvider` stores only stable IDs under a versioned localStorage key, deduplicates and validates recovered data, and synchronizes changes across browser tabs. Storage errors preserve the in-memory selection and show an alert. Favorites are browser-local, not account-backed. Legacy prototype `fav-*` keys are not migrated.

## Backend

The controller validates request boundaries and delegates to a transactional service. The service builds JPA Specifications for database filtering and allowlists sort fields. The repository handles persistence. Immutable response records keep the HTTP contract separate from the entity.

`shared/experiences.json` is the single sample-data source: Vite bundles it for demo mode and Maven copies it into the API classpath. Stable IDs let favorites refer to the same items in either mode. Seeding runs only against an empty database and can be disabled. Changes to seed JSON do not overwrite existing database records.

H2 runs in memory by default and resets when the API process exits. Environment variables support MySQL. CORS allows explicit development origins, with configuration for hosted environments. GET endpoints are public and intentionally read-only.

## Deployment boundaries

The Docker frontend serves SPA routes and proxies `/api` to the backend. Both containers run as non-root users. The Compose stack uses ephemeral H2 data for demonstration. For production persistence, configure MySQL, introduce versioned schema migrations, use `DB_DDL_AUTO=validate`, manage credentials outside source control, and add pagination before scaling the catalog.

## Quality checks

Frontend tests cover filter composition, free-budget handling, sorting, search, corrupted storage, and the save/remove interaction. API integration tests exercise actual HTTP controller behavior with H2, validation, CORS, errors, and health. CI builds both applications and preserves test reports and the frontend bundle.
