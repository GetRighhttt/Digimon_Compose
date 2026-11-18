package com.getrighhttt.digimoncompose.app

import io.ktor.client.engine.HttpClientEngine

/**
 * Each platform supplies the HTTP engine for the shared client.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
internal expect fun createDapiEngine(): HttpClientEngine
