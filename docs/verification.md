# Verification record

Last implementation checks: October 19, 2026. The code cleanup passed the formatting check, all 50 common tests, the Android debug build, shared iOS compilation, and the complete unsigned iOS host build. Catalog and profile rendering were checked again on the Samsung.

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

The current Android build was installed on a physical Samsung SM-G990U. Light-theme catalog artwork, 20 loaded entries, compact controls, system bars, the Agumon splash, and Agumon's profile were visually inspected. The README gallery contains actual device screenshots.

An initial catalog request once displayed retry while stage options loaded. Subsequent cold launches loaded the catalog. The cause was not established; the UI exposes the failure rather than treating it as empty results.

The earlier fake-data milestone ran on an Android API 36 emulator and an iPhone 17 Pro simulator. Search, combined filtering to an empty result, opening selection, and returning with the query were checked on that iOS milestone. Those checks predate the current profile and redesign. Both virtual devices were stopped when testing moved to the Samsung.

## Limits and screenshot history

Current iOS rendering/interactions, dark appearance, accessibility, large text, and complete process restoration have not had a full device pass. Host tests are not instrumented UI tests. iOS compilation/linking confirms build integration, not interactive behavior.

Current images: [splash](screenshots/splash-samsung-animation.png), [catalog](screenshots/browse-samsung-catalog.png), and [Agumon profile](screenshots/profile-samsung-agumon.png).

Historical images: [first fake Android UI](screenshots/browse-android.png), [first fake iOS UI](screenshots/browse-ios.png), and [earlier live Samsung layout](screenshots/browse-samsung-live.png). These are records of earlier milestones and do not show the current design.
