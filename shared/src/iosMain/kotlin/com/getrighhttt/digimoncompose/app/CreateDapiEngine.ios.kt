package com.getrighhttt.digimoncompose.app

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

/**
 * Creates the iOS Darwin engine owned by the shared session.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
internal actual fun createDapiEngine(): HttpClientEngine = Darwin.create()
