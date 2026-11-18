package com.getrighhttt.digimoncompose.domain.browse.repository

import com.getrighhttt.digimoncompose.domain.browse.model.BrowsePage
import com.getrighhttt.digimoncompose.domain.browse.model.BrowseResult
import com.getrighhttt.digimoncompose.domain.browse.model.PageKey
import com.getrighhttt.digimoncompose.domain.browse.model.SearchCriteria

/**
 * Loads catalog pages and stage filters. Use a next page key with the filters that
 * produced it.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
interface BrowseRepository {
    suspend fun browse(
        criteria: SearchCriteria,
        page: PageKey,
    ): BrowseResult<BrowsePage>

    suspend fun levels(): BrowseResult<List<String>>
}
