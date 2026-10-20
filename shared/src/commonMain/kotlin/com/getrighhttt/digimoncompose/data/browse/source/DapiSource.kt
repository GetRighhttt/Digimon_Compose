package com.getrighhttt.digimoncompose.data.browse.source

import com.getrighhttt.digimoncompose.data.browse.model.*
import com.getrighhttt.digimoncompose.data.profile.model.*
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

/** HTTP status is checked before decoding; error bodies can be plain text. */
internal class DapiSource(private val client: HttpClient) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun browse(name: String, level: String?, page: Int, pageSize: Int): DapiBrowseDto = read {
        client.get("https://digi-api.com/api/v1/digimon") {
            if (name.isNotBlank()) parameter("name", name)
            if (level != null) parameter("level", level)
            parameter("page", page)
            parameter("pageSize", pageSize)
        }.let { response ->
            if (!response.status.isSuccess()) throw DapiException.Http(response.status.value)
            json.decodeFromString<DapiBrowseDto>(response.bodyAsText())
        }
    }

    suspend fun levels(page: Int): DapiLevelsDto = read {
        client.get("https://digi-api.com/api/v1/level") { parameter("page", page) }.let { response ->
            if (!response.status.isSuccess()) throw DapiException.Http(response.status.value)
            json.decodeFromString<DapiLevelsDto>(response.bodyAsText())
        }
    }

    suspend fun profile(id: Int): DapiProfileDto = read {
        val response = client.get("https://digi-api.com/api/v1/digimon/$id")
        val body = response.bodyAsText()
        if (!response.status.isSuccess()) {
            if (response.status.value == 400) {
                val error = runCatching { json.decodeFromString<DapiErrorDto>(body) }.getOrNull()
                if (error?.message == "Digimon not found") throw DapiException.NotFound()
            }
            throw DapiException.Http(response.status.value)
        }
        json.decodeFromString<DapiProfileDto>(body)
    }

    private suspend fun <T> read(block: suspend () -> T): T = try {
        block()
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (error: DapiException) {
        throw error
    } catch (_: SerializationException) {
        throw DapiException.InvalidResponse()
    } catch (_: Exception) {
        throw DapiException.Unavailable()
    }
}
