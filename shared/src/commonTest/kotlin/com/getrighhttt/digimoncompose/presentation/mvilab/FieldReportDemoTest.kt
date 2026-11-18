package com.getrighhttt.digimoncompose.presentation.mvilab

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import pro.respawn.flowmvi.dsl.collect
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * Runs the same failure, retry, duplicate input, and exit checks against both libraries.
 *
 * @author Stefan Bayne
 * @since 2026-11-18
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FieldReportDemoTest {
    @Test
    fun flowMviReport() =
        runTest {
            val route = routeScope()
            val store = flowReportStore()
            store.start(route)
            var report: ReportState = ReportState.Ready
            backgroundScope.launch { store.collect { states.collect { report = it.report } } }

            try {
                verifyReport(
                    state = { report },
                    fail = { store.intent(ReportIntent.SimulateFailure) },
                    retry = { store.intent(ReportIntent.Retry) },
                    leave = { route.cancel() },
                )
            } finally {
                route.cancel()
            }
        }

    @Test
    fun orbitReport() =
        runTest {
            val route = routeScope()
            val dispatcher = StandardTestDispatcher(testScheduler)
            val host =
                OrbitReportHost(route) {
                    eventLoopDispatcher = { dispatcher }
                    intentLaunchingDispatcher = { dispatcher }
                }

            try {
                verifyReport(
                    state = { host.container.stateFlow.value },
                    fail = { host.load(simulateFailure = true) },
                    retry = { host.load() },
                    leave = { route.cancel() },
                )
            } finally {
                route.cancel()
            }
        }

    private fun TestScope.routeScope() =
        CoroutineScope(coroutineContext + Job(coroutineContext[Job]))

    private fun TestScope.verifyReport(
        state: () -> ReportState,
        fail: () -> Unit,
        retry: () -> Unit,
        leave: () -> Unit,
    ) {
        assertEquals(ReportState.Ready, state())
        fail()
        runCurrent()
        assertEquals(ReportState.Loading, state())

        retry()
        advanceTimeBy(1_000)
        runCurrent()
        assertEquals(ReportState.Failed, state())

        retry()
        runCurrent()
        assertEquals(ReportState.Loading, state())
        advanceTimeBy(1_000)
        runCurrent()
        assertIs<ReportState.Loaded>(state())

        fail()
        runCurrent()
        assertEquals(ReportState.Loading, state())
        leave()
        advanceTimeBy(1_000)
        runCurrent()
        assertEquals(ReportState.Loading, state())
    }
}
