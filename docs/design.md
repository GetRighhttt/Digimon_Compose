# Digimon design

Updated 2026-10-19.

## References and preferences

Stefan selected light neutral surfaces, teal accents, larger artwork, and a matching system dark theme. He wants a composition distinct from his Rick and Morty app. The current Browse layout uses one compact app bar, search, stage chips, and an artwork grid. It has no large stacked title/subtitle hero or repeated directory heading.

Reusable reference: [Figma Community mobile app designs](https://www.figma.com/community/mobile-apps?resource_type=files&editor_type=figma). The collection and its thumbnails were reviewed in the browser. This is an inspiration source, not a selected template or an imported Figma file.

## Browse

The app title, loaded count, and accessible refresh action share one horizontal app bar. Search and stage filters lead directly into the directory. Search/filter controls scroll with the cards; the app bar stays visible. Artwork cards use a source identifier, a name limited to two lines, and an open-profile action. Adaptive columns support two cards at the Samsung's current width and additional columns on wider screens.

The source summary does not include level/attribute information. Browse avoids extra detail requests per card and does not display invented metadata. Stage filtering uses DAPI's canonical names.

## Profiles

Opening a card now loads its actual profile. Artwork, classification tags, an English description, debut information, X-Antibody status, and skills use source data. Missing metadata displays an unknown/unavailable value where needed. Skills initially show three entries and can expand to the complete list.

Evolution data is branching and conditional. The current screen shows relationship counts when present; interactive evolution browsing is future work. The counts are distinct source IDs rather than a promised linear sequence.

Profiles have their own domain repository, data DTOs/mapping, presentation models, store, and pure reducer. Opening a newer profile or closing the screen invalidates pending results. Browse's state and scroll position remain alive while a profile is open. Android handles system back; iOS currently uses the visible back action.

## Splash

Agumon artwork is bundled in `shared/src/commonMain/composeResources/drawable/splash_agumon.png`, sourced from [DAPI's Agumon artwork](https://digi-api.com/images/digimon/w/Agumon.png). It is available offline and rendered through Compose's shared resource system.

The shared launch screen uses the same semantic palette and one app-name label. Its entrance and fade take about 1.1 seconds in total. Catalog requests begin concurrently and do not gate its dismissal. Saveable state prevents the animation replaying during ordinary Android configuration recreation. The standard OS launch surface precedes this shared animated splash; it is not a replacement for all native launch behavior.

## Verification

The light-theme splash, compact catalog, and Agumon profile were visually inspected on a Samsung SM-G990U and appear in the [README gallery](../README.md#screenshots). Dark appearance is implemented but has not been separately visually checked. Current iOS interactions still need a device pass. See [verification](verification.md) for build/test results and their limits.
