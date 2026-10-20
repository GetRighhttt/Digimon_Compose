package com.getrighhttt.digimoncompose.data.browse.repository

import com.getrighhttt.digimoncompose.data.browse.model.FakeDigimonRecord
import com.getrighhttt.digimoncompose.domain.browse.model.BrowseFailure
import com.getrighhttt.digimoncompose.domain.browse.model.BrowsePage
import com.getrighhttt.digimoncompose.domain.browse.model.BrowseResult
import com.getrighhttt.digimoncompose.domain.browse.model.DigimonSummary
import com.getrighhttt.digimoncompose.domain.browse.model.PageKey
import com.getrighhttt.digimoncompose.domain.browse.model.SearchCriteria
import com.getrighhttt.digimoncompose.domain.browse.repository.BrowseRepository
import kotlinx.coroutines.delay

/**
 * Deterministic demo catalog. Tests can control latency and request failures.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
class FakeBrowseRepository(
    private val latencyMillis: Long = 450,
    private val pageSize: Int = 6,
    private val shouldFail: (SearchCriteria, PageKey) -> Boolean = { _, _ -> false },
) : BrowseRepository {
    init {
        require(pageSize > 0)
        require(latencyMillis >= 0)
    }

    override suspend fun browse(
        criteria: SearchCriteria,
        page: PageKey,
    ): BrowseResult<BrowsePage> {
        delay(latencyMillis)
        if (shouldFail(criteria, page)) return BrowseResult.Failure(BrowseFailure.Unavailable)
        val matches =
            records.filter {
                it.name.contains(criteria.name.trim(), ignoreCase = true) &&
                    (criteria.level == null || it.level == criteria.level)
            }
        val offset = page.index.toLong() * pageSize
        if (offset > matches.size || offset > Int.MAX_VALUE) {
            return BrowseResult.Failure(BrowseFailure.InvalidPage)
        }
        val items = matches.drop(offset.toInt()).take(pageSize)
        val next = if (offset + items.size < matches.size) PageKey(page.index + 1) else null
        return BrowseResult.Success(BrowsePage(items.map { DigimonSummary(it.id, it.name) }, next))
    }

    override suspend fun levels(): BrowseResult<List<String>> {
        delay(latencyMillis)
        return BrowseResult.Success(records.map { it.level }.distinct())
    }

    private val records =
        listOf(
            FakeDigimonRecord(1, "Agumon", "Child"),
            FakeDigimonRecord(2, "Airdramon", "Adult"),
            FakeDigimonRecord(3, "Angemon", "Adult"),
            FakeDigimonRecord(4, "Betamon", "Child"),
            FakeDigimonRecord(5, "Birdramon", "Adult"),
            FakeDigimonRecord(6, "Gabumon", "Child"),
            FakeDigimonRecord(7, "Garurumon", "Adult"),
            FakeDigimonRecord(8, "Gomamon", "Child"),
            FakeDigimonRecord(9, "Greymon", "Adult"),
            FakeDigimonRecord(10, "Kabuterimon", "Adult"),
            FakeDigimonRecord(11, "Koromon", "Baby II"),
            FakeDigimonRecord(12, "Palmon", "Child"),
            FakeDigimonRecord(13, "Patamon", "Child"),
            FakeDigimonRecord(14, "Tentomon", "Child"),
            FakeDigimonRecord(15, "Togemon", "Adult"),
            FakeDigimonRecord(16, "Tokomon", "Baby II"),
            FakeDigimonRecord(17, "MetalGreymon", "Perfect"),
            FakeDigimonRecord(18, "WarGreymon", "Ultimate"),
        )
}
