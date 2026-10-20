# Browse MVI implementation

## Follow a search through the code

1. `BrowseScreen` receives a `BrowseState` and dispatch callback. Editing the search field sends `BrowseIntent.QueryChanged`; the screen never calls a repository.
2. `BrowseStore` puts the intent into its channel. Its single loop handles the intent, cancels obsolete jobs, assigns a new generation/request identity, and applies `BeginReplacement` through `reduceBrowse`.
3. The reducer clears obsolete cards and publishes the new criteria with `Debouncing`. The store starts a child coroutine with a 300 ms delay. Clearing the query or changing the level skips that delay.
4. The operation maps presentation criteria to domain criteria, calls the repository, then maps domain summaries into presentation cards. It reports a success/failure event into the same loop.
5. The reducer checks generation and request identity before accepting the event. Accepted state reaches Compose through a read-only StateFlow. An obsolete event leaves state unchanged.

The reducer does no I/O or scheduling. The store owns request coordination; the repository owns catalog access and filtering. Model files in presentation do not import domain/data. The mapper and store bridge the layers.

## Files to read

- [BrowseIntent](../shared/src/commonMain/kotlin/com/getrighhttt/digimoncompose/presentation/browse/model/BrowseIntent.kt)
- [BrowseState](../shared/src/commonMain/kotlin/com/getrighhttt/digimoncompose/presentation/browse/model/BrowseState.kt)
- [BrowseStore](../shared/src/commonMain/kotlin/com/getrighhttt/digimoncompose/presentation/browse/BrowseStore.kt)
- [BrowseReducer](../shared/src/commonMain/kotlin/com/getrighhttt/digimoncompose/presentation/browse/BrowseReducer.kt)
- [FakeBrowseRepository](../shared/src/commonMain/kotlin/com/getrighhttt/digimoncompose/data/browse/repository/FakeBrowseRepository.kt)

## Why both cancellation and identity checks exist

Cancellation prevents unnecessary work and stops pending debounce delays. A response can still race cancellation, or a dependency may not cooperate with it. Generation and request checks protect state independently of cancellation. Append responses also need to match the expected page. This matters when refresh interrupts an append or a new filter replaces the catalog.

An append failure retains accepted cards and the continuation for explicit retry. Refresh keeps cards visible but cancels append and resets pagination. Empty successful results have a different state from errors. Loading filter options can fail independently of the grid.

## Lifecycle and selection

Android's `BrowseViewModel` owns the store across configuration changes. The iOS controller creates the store with its composition scope and closes it when disposed. The store creates its own child job, so closing it never cancels the injected parent scope. Tests supply a test scope.

The shared root keeps the Browse store and saveable grid state alive when showing a profile. Selection itself is saveable UI state, outside the Browse reducer. Android handles system back; iOS currently has the visible back action. Profiles load through their own store; see [Profile MVI](profile-mvi.md). Interactive iOS back gestures and full navigation-entry ownership remain future work.

Transient effects are unnecessary for this milestone. Loading, errors, and retry progress are persistent state. Future one-time effects must specify what happens when collection stops.

## Tests and verification

Browse has 40 deterministic host tests: 16 store tests, four reducer tests, two fake repository tests, and 18 DAPI source/repository tests. They cover debounce timing, combined criteria, cancellation, late success/failure, refresh, append retry, nonadvancing continuations, overlapping items, empty results, final pages, and level retry.

Reducer tests check that accepted page data does not mutate previous state. Store tests control completion order and virtual time. The fake repository accepts configurable latency, page size, and a failure predicate; product UI contains no testing switches. See [verification](verification.md) for profile coverage, builds, device checks, and limits.

## Deferred behavior

`contentOrigin` is not present until caching exists. Live artwork and defensive API parsing are implemented; see [live Browse](live-browse.md). The fake catalog uses fixture IDs rather than live source identity. Full navigation, iOS back gestures, persistence, localization, and complete process recreation remain follow-up work.
