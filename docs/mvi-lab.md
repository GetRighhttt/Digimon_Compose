# Compare FlowMVI and Orbit in DigimonCompose

From the top of the catalog, open **FlowMVI** or **Orbit** under **MVI lab**. Both buttons open the same Field report UI, backed by different libraries. The regular catalog and profile still use the app's existing stores.

## Run the team demo

1. Open **FlowMVI** and tap **Load report**. Watch the screen go from ready to loading to an Agumon sighting.
2. Tap **Simulate failure**, then **Retry**. The error is part of screen state, so the retry button stays available until another request begins.
3. Start another load and immediately go back. Reopen the demo and verify that it starts with no report. Try the same steps in **Orbit**.
4. Compare the two implementations and decide which one the team would prefer to read, test, and debug in an existing feature.

The request waits 900 ms and returns a fixed result. It works offline so both demos behave the same during a presentation. The route owns the coroutine scope; leaving cancels pending work and discards the store or container. Screen collection stops below the started lifecycle state. In-flight work can continue if the app backgrounds without leaving the route.

## Follow one tap in the code

The [shared renderer](../shared/src/commonMain/kotlin/com/getrighhttt/digimoncompose/presentation/mvilab/FieldReportScreen.kt) receives immutable state and callbacks. It knows neither framework. [FieldReportDemo](../shared/src/commonMain/kotlin/com/getrighhttt/digimoncompose/presentation/mvilab/FieldReportDemo.kt) connects those callbacks to the selected implementation.

| Question | FlowMVI 3.2.1 | Orbit 12.0.1 |
| --- | --- | --- |
| Where does a tap go? | The UI sends a typed intent to a store. The `reduce` plugin handles it. | The UI calls a host method that runs an `intent` block. |
| What changes state? | `updateState` publishes loading and the result inside the intent handler. | `reduce` publishes loading and the result inside the intent block. |
| How does Compose read state? | It opens a store subscription and reads the provider's state flow. | It collects the container's external state flow. The internal and external models are the same here. |
| What prevents overlapping loads? | This store enables parallel intent processing and uses a mutex to drop another request while one is running. Sequential intent processing is FlowMVI's default. | Orbit intent blocks can overlap when they suspend. The host uses the same mutex policy. |

Read [FlowReportStore](../shared/src/commonMain/kotlin/com/getrighhttt/digimoncompose/presentation/mvilab/FlowReportStore.kt) and [OrbitReportHost](../shared/src/commonMain/kotlin/com/getrighhttt/digimoncompose/presentation/mvilab/OrbitReportHost.kt) side by side. Both await the request outside the state update. The [shared behavior tests](../shared/src/commonTest/kotlin/com/getrighhttt/digimoncompose/presentation/mvilab/FieldReportDemoTest.kt) cover failure, retry, overlapping input, and cancellation against both libraries.

## What to pilot before choosing

The demo compares the shape of the framework APIs, request ownership, and state delivery. It doesn't evaluate one-off effects, state restoration, or how either library fits a screen that survives navigation. Try an existing feature with all three before committing to the migration. Use the same behavior tests against both versions and compare the amount of glue, where cancellation decisions live, and how easy it is to trace an input to a rendered state.

References: [FlowMVI quickstart](https://opensource.respawn.pro/FlowMVI/quickstart) and [Orbit operators](https://orbit-mvi.org/Core/).
