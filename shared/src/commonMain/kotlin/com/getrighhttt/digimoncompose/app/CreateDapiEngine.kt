package com.getrighhttt.digimoncompose.app

import io.ktor.client.engine.HttpClientEngine

/**
 * Defines the platform HTTP-engine factory used by the shared session.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
internal expect fun createDapiEngine(): HttpClientEngine
