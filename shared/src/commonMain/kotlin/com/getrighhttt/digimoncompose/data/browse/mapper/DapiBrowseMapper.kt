package com.getrighhttt.digimoncompose.data.browse.mapper

import com.getrighhttt.digimoncompose.data.browse.model.*
import com.getrighhttt.digimoncompose.domain.browse.model.*
import io.ktor.http.URLProtocol
import io.ktor.http.Url

/**
 * Validates catalog metadata and maps accepted summaries into domain values. Continuations are reconstructed locally because DAPI can duplicate pageSize in its links.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
internal fun DapiPageDto.continuation(
    requestedPage: Int, expectedPath: String, name: String? = null, level: String? = null,
): Int? {
    if (currentPage != requestedPage || elementsOnPage < 0 || totalElements < elementsOnPage) {
        throw DapiException.InvalidResponse()
    }
    if (nextPage.isBlank()) return null
    val url = try { Url(nextPage) } catch (_: Exception) { throw DapiException.InvalidResponse() }
    if (url.protocol != URLProtocol.HTTPS || url.host != "digi-api.com" || url.encodedPath != expectedPath) {
        throw DapiException.InvalidResponse()
    }
    val next = url.parameters.getAll("page")?.singleOrNull()?.toIntOrNull()
    if (next == null || requestedPage == Int.MAX_VALUE || next != requestedPage + 1) {
        throw DapiException.InvalidResponse()
    }
    for ((key, expected) in listOf("name" to name, "level" to level)) {
        val supplied = url.parameters.getAll(key)
        if (supplied != null && (supplied.size != 1 || supplied.single().lowercase() != expected.orEmpty())) {
            throw DapiException.InvalidResponse()
        }
    }
    return next
}

internal fun DapiBrowseDto.toDomain(page: Int, name: String, level: String?): BrowsePage {
    val rows = content.orEmpty()
    if (rows.size != pageable.elementsOnPage || rows.any { it.id <= 0 || it.name.isBlank() }) {
        throw DapiException.InvalidResponse()
    }
    val next = pageable.continuation(page, "/api/v1/digimon", name, level)
    return BrowsePage(rows.map { DigimonSummary(it.id, it.name, it.image?.takeIf(::isHttpsImage)) }, next?.let(::PageKey))
}

private fun isHttpsImage(value: String): Boolean = try {
    val url = Url(value)
    url.protocol == URLProtocol.HTTPS && url.host.isNotBlank()
} catch (_: Exception) { false }
