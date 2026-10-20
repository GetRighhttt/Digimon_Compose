package com.getrighhttt.digimoncompose.app

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

internal actual fun createDapiEngine(): HttpClientEngine = OkHttp.create()
