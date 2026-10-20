package com.getrighhttt.digimoncompose.data.browse

import com.getrighhttt.digimoncompose.data.browse.repository.FakeBrowseRepository
import com.getrighhttt.digimoncompose.domain.browse.model.BrowseFailure
import com.getrighhttt.digimoncompose.domain.browse.model.BrowsePage
import com.getrighhttt.digimoncompose.domain.browse.model.BrowseResult
import com.getrighhttt.digimoncompose.domain.browse.model.PageKey
import com.getrighhttt.digimoncompose.domain.browse.model.SearchCriteria
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Checks combined filtering, pagination, empty results, and configured failures in the
 * local catalog.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
class FakeBrowseRepositoryTest {
    @Test
    fun combinedFiltersAreAppliedBeforePagination() =
        runTest {
            val repo = FakeBrowseRepository(latencyMillis = 0, pageSize = 2)
            val first =
                assertIs<BrowseResult.Success<BrowsePage>>(
                    repo.browse(SearchCriteria("mon", "Child"), PageKey(0)),
                ).value
            val second =
                assertIs<BrowseResult.Success<BrowsePage>>(
                    repo.browse(SearchCriteria("mon", "Child"), first.nextPage!!),
                ).value
            assertEquals(listOf("Agumon", "Betamon"), first.items.map { it.name })
            assertEquals(listOf("Gabumon", "Gomamon"), second.items.map { it.name })
        }

    @Test
    fun noMatchesIsEmptySuccessAndConfiguredFailureIsTyped() =
        runTest {
            val repo = FakeBrowseRepository(latencyMillis = 0)
            val empty =
                assertIs<BrowseResult.Success<BrowsePage>>(
                    repo.browse(SearchCriteria("zzz"), PageKey(0)),
                ).value
            assertTrue(empty.items.isEmpty())
            assertNull(empty.nextPage)
            val failing = FakeBrowseRepository(latencyMillis = 0, shouldFail = { _, _ -> true })
            assertEquals(
                BrowseResult.Failure(BrowseFailure.Unavailable),
                failing.browse(SearchCriteria(), PageKey(0)),
            )
        }
}
