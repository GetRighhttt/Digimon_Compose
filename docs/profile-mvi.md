# Profile MVI implementation

Opening a card loads a real DAPI profile through a feature separate from Browse. Browse continues to own the catalog while the shared root owns the selected profile ID and back action.

## Follow a selection

1. `DigimonApp` dispatches `ProfileIntent.Open(id)` when selection changes.
2. `ProfileStore` handles the intent in its channel loop. Opening the same active ID does not duplicate the request. Opening a different ID cancels the previous job and assigns a new request identity.
3. `ProfileEvent.Loading` replaces profile state with the selected ID and loading state.
4. The repository loads and validates the response. The store maps the domain result to a `Loaded` or `Failed` event.
5. `reduceProfile` accepts the completion only when both ID and request identity match. The read-only StateFlow reaches `ProfileScreen`.
6. `Retry` starts another request only when the current profile has an error. `Closed` cancels the job and resets state, so a late completion cannot reopen the profile.

The store propagates cancellation and maps unexpected operation exceptions to an unavailable result. The reducer performs no I/O.

## Files to read

- [Intent](../shared/src/commonMain/kotlin/com/getrighhttt/digimoncompose/presentation/profile/model/ProfileIntent.kt) and [state](../shared/src/commonMain/kotlin/com/getrighhttt/digimoncompose/presentation/profile/model/ProfileState.kt)
- [Store](../shared/src/commonMain/kotlin/com/getrighhttt/digimoncompose/presentation/profile/ProfileStore.kt) and [reducer](../shared/src/commonMain/kotlin/com/getrighhttt/digimoncompose/presentation/profile/ProfileReducer.kt)
- [Repository](../shared/src/commonMain/kotlin/com/getrighhttt/digimoncompose/data/profile/repository/DapiProfileRepository.kt) and [mapper](../shared/src/commonMain/kotlin/com/getrighhttt/digimoncompose/data/profile/mapper/DapiProfileMapper.kt)
- [Screen](../shared/src/commonMain/kotlin/com/getrighhttt/digimoncompose/presentation/profile/ProfileScreen.kt)

## Data rules

The response must match the requested positive ID and have a nonblank name. Artwork must use HTTPS. Classification lists discard blank/duplicate values. English descriptions prefer the `reference_book` origin, then the first available English entry. Missing English text has an explicit fallback.

Release date and X-Antibody status may be absent. Skills with blank names are discarded. Evolution counts use distinct related IDs; absent relationship data stays unknown rather than becoming zero. The UI initially shows three skills and can expand the list. Evolution counts do not imply a linear chain or provide navigation yet.

The source maps the observed HTTP 400 body with `Digimon not found` narrowly to NotFound. Other rejected requests, unavailable service, and invalid responses remain separate failures. See [API behavior](live-browse.md) and [verification](verification.md).
