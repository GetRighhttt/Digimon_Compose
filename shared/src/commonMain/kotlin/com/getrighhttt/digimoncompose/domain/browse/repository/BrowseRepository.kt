package com.getrighhttt.digimoncompose.domain.browse.repository

import com.getrighhttt.digimoncompose.domain.browse.model.*

/**
 * A continuation is meaningful only for the criteria that produced it.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
interface BrowseRepository {
    suspend fun browse(criteria: SearchCriteria, page: PageKey): BrowseResult<BrowsePage>
    suspend fun levels(): BrowseResult<List<String>>
}
