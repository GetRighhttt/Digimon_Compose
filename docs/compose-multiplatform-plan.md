# Implementation status and roadmap

Updated October 19, 2026.

## Purpose

Build a separate Compose Multiplatform Digimon edition to practice MVI implementation and document the decisions. Android and iOS share the feature code. The original XML application remains a historical reference.

The project is `DigimonCompose`, with application identifier `com.getrighhttt.digimoncompose`. Its three modules are `androidApp`, `iosApp`, and `shared`. Coroutines and StateFlow implement MVI directly. Ktor, Kotlin serialization, and Coil handle network data and artwork. A dependency-injection framework and database have not been added.

## Completed milestones

| Milestone | Result |
| --- | --- |
| Shared foundation | Thin Android/iOS hosts, shared Compose UI, platform engines, explicit session ownership |
| Browse with fake data | Intent/state contract, pure reducer, serialized store, deterministic tests, first device checks |
| Live Browse | DAPI source and repository, normalized search, level options, pagination validation, failures/retry |
| Profile | Independent MVI feature, profile mapping, classification, English text, skills, relationship counts |
| Visual design | Compact catalog, light/dark semantic palette, artwork cards, bundled Agumon splash |
| Documentation | Current screenshot gallery, architecture, feature walkthroughs, API notes, verification record |

See [architecture](architecture.md), [Browse MVI](browse-mvi.md), and [Profile MVI](profile-mvi.md) for the implemented contracts. [Verification](verification.md) records what has been tested.

## Remaining milestones

1. Favorites: define add/remove behavior and a Favorites screen. Decide local identity and source reconciliation before persistence, because upstream IDs can change. Store a name snapshot alongside the last known source ID.
2. Persistence and cached browsing: select the shared storage approach, distinguish cached content from a successful refresh, and specify offline/empty/error behavior. Image caching alone does not cover this.
3. Evolution navigation: present branching relationships and conditions, then define back-stack behavior between profiles.
4. Platform completion: interactive iOS back behavior, navigation-entry ownership, and restoration after the operating system terminates the app. Restore small criteria/selection values and reload data rather than saving entire result lists.
5. UI validation: current iOS interaction pass, dark appearance, accessibility, large text, keyboard/insets, and wider layouts. Add localization when the copy and canonical stage-name mappings are defined.

Desktop and web are outside the current target set. Add another target only with a concrete use case and platform verification.

## Acceptance rules for further work

Keep request cancellation and identity checks independent. An obsolete response must never replace current criteria or profile content. Empty success must remain distinct from failure. Refresh and append errors must keep their retry behavior explicit.

Extend controlled store/repository tests for new behavior. Use actual devices for rendering, navigation, lifecycle, and accessibility checks; shared compilation does not establish those behaviors.
