# Verification record

Last implementation checks: October 19, 2026. The UI update passed the formatting check, all 50 common tests, the Android debug build, shared iOS compilation, and the complete unsigned iOS host build. Grid, compact list, and profile rendering were checked on the Samsung in light appearance, with an additional dark grid/profile pass.

The later readability adjustment also passed formatting, all 50 tests, the Android debug build, and shared iOS simulator compilation. A signed iPhone build succeeded using the locally configured development team and was installed on the connected iPhone 17 Pro.

## Automated checks

All 50 common tests passed on the Android host, with no failures or skips:

| Suite | Tests |
| --- | ---: |
| Browse store | 16 |
| Browse reducer | 4 |
| Fake Browse repository | 2 |
| DAPI Browse repository/source | 18 |
| Profile store/reducer behavior | 5 |
| DAPI Profile repository/mapping | 5 |

Browse coverage includes debounce timing, criteria changes, cancellation, stale responses, refresh, append retry, overlapping items, empty/final pages, and level-option retry. HTTP tests use a mock engine for malformed continuations, omitted content, metadata mismatches, status errors, transport failure, and mapping. Profile tests cover loading/retry, closing/replacing requests, stale completions, and profile response validation/mapping.

```sh
./gradlew checkKotlinFormatting :androidApp:assembleDebug :shared:testAndroidHostTest :shared:compileKotlinIosSimulatorArm64 --max-workers=2 --console=plain
```

The complete unsigned iOS simulator application also built, including the bundled Compose splash resource:

```sh
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp \
  -configuration Debug -sdk iphonesimulator \
  -destination 'generic/platform=iOS Simulator' \
  -derivedDataPath /private/tmp/digimon-compose-derived \
  CODE_SIGNING_ALLOWED=NO build
```

A separate temporary live-client smoke test loaded the catalog, combined name/stage search, a filtered continuation, all nine stages, and Agumon's English description and 18 skills. It is not part of the deterministic suite and does not establish service uptime.

## Device checks

The current Android build was installed on a physical Samsung SM-G990U. The following checks passed:

- Open the initial 20-entry catalog, switch to the compact list, and open Agumon.
- Return from the profile and confirm the compact layout remains selected.
- Search for Agumon, combine it with the Adult stage, and confirm the empty state.
- Reset the stage, clear the query, and refresh the 20-entry catalog.
- Switch back to the artwork grid and inspect it in light and dark appearance.
- Inspect Agumon's cover, classification, and quick facts in both appearances.
- Inspect the catalog at 1.3× system font size, then restore the original font size and light appearance.

The README gallery contains actual device screenshots. The existing Agumon splash capture comes from the earlier launch-animation check; this pass did not change the splash.

On the physical iPhone 17 Pro, the app filled the screen with one safe-area inset. The denser appearance prompted larger catalog labels, more search/card padding, wider grid gaps, and larger profile body text. The updated catalog and Airdramon profile were visually inspected in dark appearance. The Samsung gallery predates this text and spacing adjustment. This was a visual check, not a complete iOS interaction or accessibility pass.

An initial catalog request once displayed retry while stage options loaded. Subsequent cold launches loaded the catalog. The cause was not established; the UI exposes the failure rather than treating it as empty results.

The earlier fake-data milestone ran on an Android API 36 emulator and an iPhone 17 Pro simulator. Search, combined filtering to an empty result, opening selection, and returning with the query were checked on that iOS milestone. Those checks predate the current profile and redesign. Both virtual devices were stopped when testing moved to the Samsung.

## Limits and screenshot history

The complete set of iOS interactions, iOS light appearance, screen-reader behavior, the full range of text sizes, and complete process restoration have not had a full device pass. The larger-text check covered the catalog at 1.3×, not every screen or accessibility setting. Host tests are not instrumented UI tests. iOS compilation/linking confirms build integration, not interactive behavior.

Current images: [splash](screenshots/splash-samsung-animation.png), [catalog](screenshots/browse-samsung-catalog.png), [compact list](screenshots/browse-samsung-list.png), [Agumon profile](screenshots/profile-samsung-agumon.png), [dark catalog](screenshots/browse-samsung-dark.png), and [dark profile](screenshots/profile-samsung-dark.png).

Historical images: [first fake Android UI](screenshots/browse-android.png), [first fake iOS UI](screenshots/browse-ios.png), and [earlier live Samsung layout](screenshots/browse-samsung-live.png). These are records of earlier milestones and do not show the current design.
