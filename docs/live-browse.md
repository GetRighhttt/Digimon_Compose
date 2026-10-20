# Live Browse

Implemented on 2026-10-19. The MVI store/reducer still own search, refresh, pagination, and request cancellation. Platform owners now construct a `BrowseSession` containing the live repository and HTTP client.

## Request and response ownership

`DapiSource` performs HTTPS requests and checks status before decoding success bodies. Its DTOs describe optional list content and the different level-options envelope. The source contains no domain or presentation imports.

`DapiBrowseRepository` normalizes criteria, applies the page size, coordinates all level pages, and maps DTOs to domain results. `DapiBrowseMapper` validates counts, current-page metadata, source identifiers, and next-page context. Unknown JSON fields are ignored. Missing content is accepted only when its item count is zero. Invalid/insecure image references become null so presentation uses a fallback.

The API sometimes returns links with `pageSize=pageSize=2`. The mapper validates the link's origin, path, next index, and any supplied search/filter values, then extracts only the advancing page index. Every subsequent request is reconstructed from the current criteria and configured page size. Response links are never followed verbatim. Misleading `totalPages` metadata does not determine completion; an empty continuation does.

Level loading follows the separate `content.fields` response through all pages. A bounded 20-page limit rejects unexpectedly unbounded metadata; a failed later page does not publish partial level options.

HTTP 408, 429, server failures, and transport failures map to unavailable. Other unsuccessful statuses map to a rejected request. Invalid JSON or metadata maps to an invalid response. Cancellation propagates. No server response body or exception message reaches UI state.

## Client and image lifetime

`BrowseSession` configures request/socket timeouts of 20 seconds and a connection timeout of 10 seconds. The API client disables automatic redirects and has no retry plugin. Android uses OkHttp; iOS uses Darwin. Closing the session closes both stores, the client, and explicitly supplied engine. Android's retained ViewModel owns the session; the iOS controller composition owns its session.

Coil loads HTTPS artwork separately using the platform Ktor engine. Cards keep a letter fallback during loading or image failure. This image cache does not provide catalog persistence or guarantee offline artwork.

## UI adjustment

Refresh and the loaded count now share the compact app bar. Search and filters lead directly into the artwork grid. Search changes reset the grid to the first item; returning from a profile keeps its grid state. The initial live-data layout has since been replaced by the compact catalog described in the design notes.

## Verification

The 40 Browse host tests cover store behavior and defensive HTTP mapping. Ten additional tests cover Profile. Android debug, shared iOS compilation, complete iOS host linking, and a separate live-client smoke check passed. See the [verification record](verification.md) for commands, device observations, and limits.

The [current Samsung catalog](screenshots/browse-samsung-catalog.png) uses the system light/dark palette and compact app bar. The earlier navy/yellow screenshot is historical.

## Remaining scope

Profiles now load through their own MVI feature; see [Profile MVI](profile-mvi.md). Favorites, persistence, offline catalog semantics, localization, and interactive evolution navigation remain future work. DAPI availability and data completeness remain external constraints; the app exposes retry rather than treating upstream failures as an empty or final page.
