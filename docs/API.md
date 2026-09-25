# API reference

The API is read-only and requires no authentication. The catalog is a small, curated sample dataset, not live venue data.

## List experiences

`GET /api/experiences`

| Parameter | Meaning | Accepted values |
| --- | --- | --- |
| `q` | Case-insensitive name, description, or neighborhood search | Up to 100 characters; SQL wildcard characters are treated literally |
| `mood` | Exact mood, case-insensitive | Up to 60 characters |
| `category` | Exact category, case-insensitive | Up to 60 characters |
| `neighborhood` | Exact neighborhood, case-insensitive | Up to 60 characters |
| `maxBudget` | Inclusive maximum budget tier | 0–3; 0 means free |
| `maxDuration` | Inclusive maximum duration in minutes | 1–1440 |
| `sort` | Ordering, with name as a tiebreaker | `name` (default), `budget`, `duration` |

Filters combine with AND. Unmatched searches return `200` and `[]`. The list is unpaginated because the bundled catalog is small; add pagination before connecting a large data source.

```bash
curl 'http://localhost:8080/api/experiences?maxBudget=0&maxDuration=90&sort=duration'
```

## Experience details

`GET /api/experiences/{id}` returns an object:

```json
{
  "id": 1,
  "name": "Chicago Riverwalk",
  "description": "Follow the river through the heart of the city, with architecture and waterfront views along the way.",
  "category": "Outdoors",
  "mood": "Explore Chicago",
  "budgetLevel": 0,
  "durationMinutes": 90,
  "neighborhood": "Loop",
  "hiddenGem": false
}
```

Invalid parameters return `400`; missing positive IDs return `404`. Errors use Spring Problem Details (`application/problem+json`). No create, edit, delete, booking, or account endpoints are implemented.

## Health

`GET /actuator/health` reports application/database health. Detailed internal health data is not exposed.
