package com.getrighhttt.digimoncompose.app

import com.getrighhttt.digimoncompose.data.browse.repository.DapiBrowseRepository
import com.getrighhttt.digimoncompose.data.browse.source.DapiSource
import com.getrighhttt.digimoncompose.presentation.browse.BrowseStore
import com.getrighhttt.digimoncompose.presentation.profile.ProfileStore
import com.getrighhttt.digimoncompose.data.profile.repository.DapiProfileRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import kotlinx.coroutines.CoroutineScope

/** Platform lifecycle owners close the store and its HTTP client together. */
class BrowseSession(parentScope: CoroutineScope) {
    private val engine = createDapiEngine()
    private val client = HttpClient(engine) {
        expectSuccess = false
        followRedirects = false
        install(HttpTimeout) {
            requestTimeoutMillis = 20_000
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 20_000
        }
    }
    private val source = DapiSource(client)
    val store = BrowseStore(DapiBrowseRepository(source), parentScope)
    val profileStore = ProfileStore(DapiProfileRepository(source), parentScope)
    fun close() { store.close(); profileStore.close(); client.close(); engine.close() }
}
