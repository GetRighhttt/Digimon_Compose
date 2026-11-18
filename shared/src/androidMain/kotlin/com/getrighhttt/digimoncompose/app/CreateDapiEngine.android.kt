package com.getrighhttt.digimoncompose.app

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

/**
 * Uses OkHttp for the shared client's Android requests.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
internal actual fun createDapiEngine(): HttpClientEngine = OkHttp.create()
