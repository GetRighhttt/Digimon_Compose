package com.getrighhttt.digimoncompose.data.browse.repository

import com.getrighhttt.digimoncompose.data.browse.mapper.continuation
import com.getrighhttt.digimoncompose.data.browse.mapper.toDomain
import com.getrighhttt.digimoncompose.data.browse.model.DapiException
import com.getrighhttt.digimoncompose.data.browse.source.DapiSource
import com.getrighhttt.digimoncompose.domain.browse.model.*
import com.getrighhttt.digimoncompose.domain.browse.repository.BrowseRepository
import kotlinx.coroutines.CancellationException

/** Canonical criteria and page size stay attached to every locally constructed request. */
internal class DapiBrowseRepository(private val source: DapiSource, private val pageSize: Int = 20) : BrowseRepository {
    init { require(pageSize in 1..100) }

    override suspend fun browse(criteria: SearchCriteria, page: PageKey): BrowseResult<BrowsePage> = result {
        val name = criteria.name.trim().lowercase()
        val level = criteria.level?.trim()?.lowercase()?.takeIf { it.isNotBlank() }
        source.browse(name, level, page.index, pageSize).toDomain(page.index, name, level)
    }

    override suspend fun levels(): BrowseResult<List<String>> = result {
        val names = mutableListOf<String>()
        var page = 0
        while (true) {
            val response = source.levels(page)
            val rows = response.content.fields
            if (rows.size != response.pageable.elementsOnPage || rows.any { it.id <= 0 || it.name.isBlank() }) {
                throw DapiException.InvalidResponse()
            }
            names += rows.map { it.name }
            val next = response.pageable.continuation(page, "/api/v1/level") ?: break
            if (next >= 20) throw DapiException.InvalidResponse()
            page = next
        }
        names.distinct()
    }

    private suspend fun <T> result(block: suspend () -> T): BrowseResult<T> = try {
        BrowseResult.Success(block())
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (error: DapiException) {
        BrowseResult.Failure(when (error) {
            is DapiException.NotFound -> BrowseFailure.RequestRejected
            is DapiException.InvalidResponse -> BrowseFailure.InvalidResponse
            is DapiException.Unavailable -> BrowseFailure.Unavailable
            is DapiException.Http -> when (error.status) {
                408, 429, in 500..599 -> BrowseFailure.Unavailable
                else -> BrowseFailure.RequestRejected
            }
        })
    }
}
