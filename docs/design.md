# Digimon design

Updated 2026-10-19.

## References and preferences

Stefan selected light neutral surfaces, teal accents, larger artwork, and a matching system dark theme. He wants a composition distinct from his Rick and Morty app. The Browse layout uses a compact identity bar, search, stage chips, and a directory with grid and list views. A pixel emblem gives the app its own mark without a large title/subtitle hero.

Reusable reference: [Figma Community mobile app designs](https://www.figma.com/community/mobile-apps?resource_type=files&editor_type=figma). The collection and its thumbnails were reviewed in the browser. This is an inspiration source, not a selected template or an imported Figma file.

## Browse

The pixel emblem, app title, field-guide label, and refresh action share one horizontal app bar. Search and pill-shaped stage filters lead into the directory. The loaded count sits beside the directory heading and the grid/list switch. All content below the app bar scrolls together.

Artwork cards have a white image area and a lightly tinted label area with the source identifier and name. Compact rows use the same entry data with a thumbnail and more room for names. Both layouts open the same profile. Adaptive grid columns show two cards at the Samsung's current width and more on wider screens. Names allow two lines; profile titles wrap without that limit. Catalog names use 17 sp text, card labels have 16 dp padding, and grid entries have 16 dp gaps. Search uses 17 sp text with 18 dp vertical padding. These shared values give the iPhone catalog more breathing room without changing the screen margins.

The layout choice is saveable UI state in the shared app root. Opening and closing a profile keeps that choice, the query, and the catalog scroll position. Switching layouts does not trigger a request. Buttons and stage filters have touch targets of at least 48 dp; the filters expose a single-selection group.

The source summary does not include level/attribute information. Browse avoids extra detail requests per card and does not display invented metadata. Stage filtering uses DAPI's canonical names.

## Profiles

Opening an entry loads its actual profile. A single cover combines the artwork, source stage, and name on a deep teal panel. Classification tags and quick facts come before the overview. Overview, skills, and evolution sections use headings and dividers instead of separate enclosing cards. Artwork, classification tags, an English description, debut information, X-Antibody status, and skills use source data. Missing metadata displays an unknown/unavailable value where needed. Skills initially show three entries and can expand to the complete list.

Evolution data is branching and conditional. The current screen shows relationship counts when present; interactive evolution browsing is future work. The counts are distinct source IDs rather than a promised linear sequence.

Profiles have their own domain repository, data DTOs/mapping, presentation models, store, and pure reducer. Opening a newer profile or closing the screen invalidates pending results. Browse's state and scroll position remain alive while a profile is open. Android handles system back; iOS currently uses the visible back action.

## Splash

Agumon artwork is bundled in `shared/src/commonMain/composeResources/drawable/splash_agumon.png`, sourced from [DAPI's Agumon artwork](https://digi-api.com/images/digimon/w/Agumon.png). It is available offline and rendered through Compose's shared resource system.

The shared launch screen uses the same semantic palette and one app-name label. Its entrance and fade take about 1.1 seconds in total. Catalog requests begin concurrently and do not gate its dismissal. Saveable state prevents the animation replaying during ordinary Android configuration recreation. The standard OS launch surface precedes this shared animated splash; it is not a replacement for all native launch behavior.

## Verification

The catalog and Agumon profile were visually inspected in light and dark appearance on a Samsung SM-G990U. The compact list and catalog at 1.3× system font size were also checked. The [README gallery](../README.md#screenshots) includes current grid, list, and profile captures alongside the existing splash capture. Artwork uses a white backdrop in both appearances because source images can include white backgrounds. The catalog and Airdramon profile were also checked in dark appearance on a physical iPhone 17 Pro after the text and spacing adjustment. The remaining iOS interactions still need a full device pass. See [verification](verification.md) for build/test results and their limits.
