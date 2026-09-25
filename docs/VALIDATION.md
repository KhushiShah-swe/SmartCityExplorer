# Validation record

Validated on September 25, 2026.

| Check | Result |
| --- | --- |
| Frontend TypeScript and Vite production build | Passed |
| Vitest / Testing Library | 11 tests passed |
| Maven verify and executable Spring Boot JAR | Passed |
| MockMvc / H2 integration tests | 12 tests passed |
| Browser discovery, filter URLs, favorites, reload, removal, detail route, empty states, corrupted storage | Passed |
| Desktop 1440px and mobile 390px views | Captured; no mobile horizontal overflow |
| Real Java API with Vite proxy | Passed; catalog and filtered API verified |

The browser checks exercised both live API mode and the explicitly labeled fallback demo mode. Screenshots were captured from the running application connected to the API.

Docker builds and MySQL integration were not executed in this environment. GitHub-hosted CI has not run until the repository is published. No claim of a completed accessibility audit or production load test is made.

The test-only Mockito subclass mock maker avoids runtime Java agent attachment. Tests use the real application context and H2 rather than mocked repositories.
