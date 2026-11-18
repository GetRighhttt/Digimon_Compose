package com.getrighhttt.digimoncompose.app

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

/**
 * Uses Darwin for the shared client's iOS requests.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
internal actual fun createDapiEngine(): HttpClientEngine = Darwin.create()
