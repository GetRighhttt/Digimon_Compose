# DAPI evaluation

Live GET requests checked on 2026-10-19. This is a response snapshot, not an availability guarantee. Documentation: [DAPI](https://digi-api.com/).

Decision: use DAPI for the first network implementation, with defensive parsing and explicit pagination errors. It supports the planned Browse and Details features. Keep the fake repository milestone independent of its availability.

## Observed responses

| Request under `/api/v1` | Observation |
| --- | --- |
| `digimon?page=0&pageSize=2` | HTTP 200; Agumon and Airdramon. Summary fields: `id`, `name`, `href`, `image`. |
| `digimon?page=1&pageSize=2` | HTTP 200; Angemon and Betamon; continuation advances to page 2. |
| `digimon?name=agumon&pageSize=2` | HTTP 200; 9 matches, including variants. |
| `digimon?name=agumon&level=child&pageSize=2` | HTTP 200; 7 matches. Name and level filters combine. |
| Same criteria with `page=1` | HTTP 200; different matching variants and continuation to page 2. |
| Same criteria with `page=3` | HTTP 200; one final item, `nextPage` is empty. |
| `digimon?name=agumon&level=adult&pageSize=2` | HTTP 200; no matches; `content` is omitted. |
| Unknown name `zzzcodexnomatchzzz` | HTTP 200; zero elements; `content` omitted. |
| `digimon?name=&pageSize=2` | HTTP 500 with plain-text `Internal Error`. |
| `digimon?pageSize=2` | HTTP 200; omitting blank name works. |
| Uppercase query and uppercase level | Inconsistent outcomes across tested criteria: empty success or HTTP 500. Case-insensitive filtering is not established by these probes. |
| `digimon?page=742&pageSize=2` | HTTP 200; continuation advertises page 743. |
| `digimon?page=743&pageSize=2` | HTTP 500 on two attempts. |
| `digimon?page=744&pageSize=2` | HTTP 500. |
| `digimon/1` | HTTP 200; detailed Agumon profile. |
| `digimon/999999` | HTTP 400 with a JSON not-found error. |
| `level?pageSize=20` | HTTP 200; only the first five levels. Do not assume requested page size is honored. |
| `level?page=1` | HTTP 200; remaining four levels and no continuation. |

Several returned continuation URLs contain malformed query values such as `pageSize=pageSize=2`. The final filtered page reports `totalPages=3` for pages 0 through 3. Treat that field as observed last-page metadata, not an ordinary page count; do not expose it as a page-count label.

## Data and presentation decisions

- Browse cards show name and artwork. Level is absent from the summary response; do not add per-card detail requests.
- Canonical level names observed are Baby II, Adult, Perfect, Child, Baby I, Ultimate, Armor, Unknown, and Hybrid. Use source names initially. Localized aliases require a deliberate mapping; sending `rookie` did not produce a usable result in the tested request.
- Agumon details include images, levels, types, attributes, fields, release year, descriptions tagged by language/origin, skills, and prior/next evolutions. Select English descriptions by language tag, with a missing-description state. One successful profile does not prove completeness across the catalog.
- Evolution data has multiple branches and conditions. A later feature should present relationships rather than invent one linear evolution chain.
- Favorites need a local identity plus the last known source ID and name snapshot. The documented historical ID changes mean ID permanence cannot be assumed. On a name/ID mismatch, retain the saved snapshot and require reconciliation rather than silently replacing it with a different Digimon.

## Repository behavior

1. Trim and lowercase a nonblank name for requests; omit blank name and absent level parameters. Keep user-entered text in presentation state. The live repository implements this normalization; broader mixed-case query behavior remains unverified.
2. Obtain level options from both observed pages of the level endpoint, using its `content.fields` shape. Store canonical values separately from display labels. Loading/retry for these options needs its own state and must not prevent unfiltered browsing.
3. Model summary `content` as optional. A valid HTTP 200 envelope reporting zero elements with omitted content maps to an empty domain page. Missing content with nonzero counts is a malformed-response failure, not an empty success.
4. Own pagination in data. Start at page 0, keep page size and normalized criteria in request context, and construct subsequent URLs locally. Validate the returned next-page index is advancing and consistent with context; never follow the returned URL verbatim. An empty next-page value ends pagination.
5. A failed advertised final page remains a retryable append failure. Keep existing cards; do not translate HTTP 500 into end-of-list. This leaves a visible upstream limitation if page 743 continues failing.
6. Read HTTP status before parsing success bodies. Support JSON and plain-text errors. Interpret the observed detail not-found HTTP 400 narrowly; unrelated 400 responses are request errors. Keep cancellation distinct from failures.
7. Use bounded timeouts and explicit user retry initially. Add no automatic retry loop for repeatable server errors. Missing/failed images use a local fallback.

## Implemented coverage and remaining unknowns

Repository/source tests now cover malformed links, omitted content, narrowly classified HTTP 400 not-found, plain-text errors, and retryable failures. A separate live-client smoke check passed through the actual client. See [verification](verification.md).

These probes do not establish service uptime, quotas, response behavior for every query, missing-field frequency, image availability, or durable source identity. The observations above remain a dated snapshot.
