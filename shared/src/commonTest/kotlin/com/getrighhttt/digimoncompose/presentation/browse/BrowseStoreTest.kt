package com.getrighhttt.digimoncompose.presentation.browse

import com.getrighhttt.digimoncompose.domain.browse.model.*
import com.getrighhttt.digimoncompose.domain.browse.repository.BrowseRepository
import com.getrighhttt.digimoncompose.presentation.browse.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import kotlin.test.*

/** Controlled completions exercise request ordering without real time or a live service. */
@OptIn(ExperimentalCoroutinesApi::class)
class BrowseStoreTest {
    @Test fun repeatedStartLoadsOnce() = runTest {
        val repo = ControlledRepository()
        val store = BrowseStore(repo, backgroundScope)
        store.dispatch(BrowseIntent.Started)
        store.dispatch(BrowseIntent.Started)
        runCurrent()
        assertEquals(1, repo.requests.size)
        assertEquals(1, repo.levelCalls)
        store.close()
    }

    @Test fun rapidTypingDebouncesTheLatestQueryAndPreservesDisplayText() = runTest {
        val repo = ControlledRepository()
        val store = BrowseStore(repo, backgroundScope)
        store.dispatch(BrowseIntent.QueryChanged("agu"))
        runCurrent()
        advanceTimeBy(200)
        store.dispatch(BrowseIntent.QueryChanged(" Agumon "))
        runCurrent()
        assertEquals(" Agumon ", store.state.value.criteria.query)
        advanceTimeBy(299); runCurrent()
        assertTrue(repo.requests.isEmpty())
        advanceTimeBy(1); runCurrent()
        assertEquals(SearchCriteria("agumon"), repo.requests.single().criteria)
        store.close()
    }

    @Test fun levelChangeCancelsDebounceAndStartsCombinedSearchImmediately() = runTest {
        val repo = ControlledRepository()
        val store = BrowseStore(repo, backgroundScope)
        store.dispatch(BrowseIntent.QueryChanged("agumon")); runCurrent()
        advanceTimeBy(100)
        store.dispatch(BrowseIntent.LevelChanged("Child")); runCurrent()
        assertEquals(SearchCriteria("agumon", "Child"), repo.requests.single().criteria)
        advanceTimeBy(500); runCurrent()
        assertEquals(1, repo.requests.size)
        store.close()
    }

    @Test fun clearingQueryLoadsImmediatelyAndKeepsLevel() = runTest {
        val repo = ControlledRepository()
        val store = BrowseStore(repo, backgroundScope)
        store.dispatch(BrowseIntent.LevelChanged("Child")); runCurrent()
        store.dispatch(BrowseIntent.QueryChanged("agu")); runCurrent()
        store.dispatch(BrowseIntent.QueryChanged("")); runCurrent()
        assertEquals(SearchCriteria("", "Child"), repo.requests.last().criteria)
        assertEquals(2, repo.requests.size)
        store.close()
    }

    @Test fun oldSuccessCannotReplaceNewerSearchEvenWhenCancellationIsIgnored() = runTest {
        val repo = ControlledRepository(ignoreCancellation = true)
        val store = BrowseStore(repo, backgroundScope)
        store.dispatch(BrowseIntent.Started); runCurrent()
        store.dispatch(BrowseIntent.LevelChanged("Child")); runCurrent()
        repo.requests[1].complete(page(2, "New")); runCurrent()
        repo.requests[0].complete(page(1, "Old")); runCurrent()
        assertEquals(listOf("New"), store.state.value.items.map { it.name })
        store.close()
    }

    @Test fun oldFailureCannotReplaceNewSuccess() = runTest {
        val repo = ControlledRepository(ignoreCancellation = true)
        val store = BrowseStore(repo, backgroundScope)
        store.dispatch(BrowseIntent.Started); runCurrent()
        store.dispatch(BrowseIntent.Refresh); runCurrent()
        repo.requests[1].complete(page(2, "New")); runCurrent()
        repo.requests[0].result.complete(BrowseResult.Failure(BrowseFailure.Unavailable)); runCurrent()
        assertEquals(BrowseLoadState.Ready, store.state.value.load)
        store.close()
    }

    @Test fun emptySuccessAndFailureHaveDifferentStatesAndRetryUsesCurrentCriteria() = runTest {
        val repo = ControlledRepository()
        val store = BrowseStore(repo, backgroundScope)
        store.dispatch(BrowseIntent.LevelChanged("Child")); runCurrent()
        repo.requests[0].complete(BrowsePage(emptyList(), null)); runCurrent()
        assertTrue(store.state.value.isEmpty)
        store.dispatch(BrowseIntent.Refresh); runCurrent()
        repo.requests[1].result.complete(BrowseResult.Failure(BrowseFailure.Unavailable)); runCurrent()
        assertFalse(store.state.value.isEmpty)
        assertIs<BrowseLoadState.Failed>(store.state.value.load)
        store.dispatch(BrowseIntent.RetryReplacement); runCurrent()
        assertEquals(SearchCriteria("", "Child"), repo.requests.last().criteria)
        assertEquals(BrowseLoadState.Loading, store.state.value.load)
        store.close()
    }

    @Test fun refreshKeepsCardsAndFailureOffersReplacementRetry() = runTest {
        val repo = ControlledRepository()
        val store = BrowseStore(repo, backgroundScope)
        store.dispatch(BrowseIntent.Started); runCurrent()
        repo.requests[0].complete(page(1, "Agumon", 1)); runCurrent()
        store.dispatch(BrowseIntent.Refresh); runCurrent()
        assertEquals("Agumon", store.state.value.items.single().name)
        assertEquals(BrowseLoadState.Refreshing, store.state.value.load)
        repo.requests[1].result.complete(BrowseResult.Failure(BrowseFailure.Unavailable)); runCurrent()
        assertEquals("Agumon", store.state.value.items.single().name)
        store.dispatch(BrowseIntent.RetryReplacement); runCurrent()
        assertEquals(BrowseLoadState.Refreshing, store.state.value.load)
        repo.requests[2].complete(page(2, "Gabumon")); runCurrent()
        assertEquals(listOf(2), store.state.value.items.map { it.id })
        store.close()
    }

    @Test fun duplicateLoadMoreIsIgnoredAndFailedAppendRetriesSamePage() = runTest {
        val repo = ControlledRepository()
        val store = BrowseStore(repo, backgroundScope)
        store.dispatch(BrowseIntent.Started); runCurrent()
        repo.requests[0].complete(page(1, "Agumon", 1)); runCurrent()
        repeat(3) { store.dispatch(BrowseIntent.LoadNextPage) }; runCurrent()
        assertEquals(2, repo.requests.size)
        repo.requests[1].result.complete(BrowseResult.Failure(BrowseFailure.Unavailable)); runCurrent()
        assertEquals(1, store.state.value.items.size)
        store.dispatch(BrowseIntent.LoadNextPage); runCurrent()
        assertEquals(2, repo.requests.size)
        store.dispatch(BrowseIntent.RetryAppend); runCurrent()
        assertEquals(PageKey(1), repo.requests.last().page)
        repo.requests[2].complete(page(2, "Gabumon")); runCurrent()
        assertEquals(listOf(1, 2), store.state.value.items.map { it.id })
        store.close()
    }

    @Test fun refreshRejectsLateAppendAndResetsPagination() = runTest {
        val repo = ControlledRepository(ignoreCancellation = true)
        val store = BrowseStore(repo, backgroundScope)
        store.dispatch(BrowseIntent.Started); runCurrent()
        repo.requests[0].complete(page(1, "First", 1)); runCurrent()
        store.dispatch(BrowseIntent.LoadNextPage); runCurrent()
        store.dispatch(BrowseIntent.Refresh); runCurrent()
        repo.requests[2].complete(page(3, "Refresh", 1)); runCurrent()
        repo.requests[1].complete(page(2, "Old append", 2)); runCurrent()
        assertEquals(listOf(3), store.state.value.items.map { it.id })
        assertEquals(1, store.state.value.nextPage)
        store.close()
    }

    @Test fun nonadvancingContinuationFailsWithoutLosingCardsOrStartingLoop() = runTest {
        val repo = ControlledRepository()
        val store = BrowseStore(repo, backgroundScope)
        store.dispatch(BrowseIntent.Started); runCurrent()
        repo.requests[0].complete(page(1, "First", 1)); runCurrent()
        store.dispatch(BrowseIntent.LoadNextPage); runCurrent()
        repo.requests[1].complete(page(2, "Invalid", 1)); runCurrent()
        assertEquals(AppendState.Failed(BrowseError.InvalidPage), store.state.value.append)
        assertEquals(listOf(1), store.state.value.items.map { it.id })
        store.dispatch(BrowseIntent.LoadNextPage); runCurrent()
        assertEquals(2, repo.requests.size)
        store.close()
    }

    @Test fun finalPageAndRepeatedCriteriaDoNotIssueMoreRequests() = runTest {
        val repo = ControlledRepository()
        val store = BrowseStore(repo, backgroundScope)
        store.dispatch(BrowseIntent.Started); runCurrent()
        repo.requests[0].complete(page(1, "Final")); runCurrent()
        store.dispatch(BrowseIntent.LoadNextPage)
        store.dispatch(BrowseIntent.QueryChanged(""))
        store.dispatch(BrowseIntent.LevelChanged(null)); runCurrent()
        assertEquals(1, repo.requests.size)
        store.close()
    }

    @Test fun closingStoreCancelsWorkButLeavesParentAlive() = runTest {
        val repo = ControlledRepository()
        val store = BrowseStore(repo, backgroundScope)
        store.dispatch(BrowseIntent.Started); runCurrent()
        store.close(); runCurrent()
        assertTrue(repo.cancelled)
        assertTrue(backgroundScope.isActive)
        store.dispatch(BrowseIntent.Refresh); runCurrent()
        assertEquals(1, repo.requests.size)
    }

    @Test fun cancellingParentCancelsRequest() = runTest {
        val parent = CoroutineScope(backgroundScope.coroutineContext + Job())
        val repo = ControlledRepository()
        val store = BrowseStore(repo, parent)
        store.dispatch(BrowseIntent.Started); runCurrent()
        parent.cancel(); runCurrent()
        assertTrue(repo.cancelled)
        store.close()
    }

    @Test fun levelFailureDoesNotBlockBrowseAndCanRetryIndependently() = runTest {
        val repo = ControlledRepository()
        repo.levelResult = BrowseResult.Failure(BrowseFailure.Unavailable)
        val store = BrowseStore(repo, backgroundScope)
        store.dispatch(BrowseIntent.Started); runCurrent()
        repo.requests[0].complete(page(1, "Agumon")); runCurrent()
        assertEquals(BrowseLoadState.Ready, store.state.value.load)
        assertIs<LevelOptionsState.Failed>(store.state.value.levels)
        repo.levelResult = BrowseResult.Success(listOf("Child"))
        store.dispatch(BrowseIntent.RetryLevels); runCurrent()
        assertEquals(LevelOptionsState.Ready(listOf("Child")), store.state.value.levels)
        assertEquals(1, repo.requests.size)
        store.close()
    }

    @Test fun refreshDuringDebounceStartsCurrentQueryImmediately() = runTest {
        val repo = ControlledRepository()
        val store = BrowseStore(repo, backgroundScope)
        store.dispatch(BrowseIntent.QueryChanged("Agumon")); runCurrent()
        store.dispatch(BrowseIntent.Refresh); runCurrent()
        assertEquals("agumon", repo.requests.single().criteria.name)
        advanceTimeBy(500); runCurrent()
        assertEquals(1, repo.requests.size)
        store.close()
    }

    private fun page(id: Int, name: String, next: Int? = null) =
        BrowsePage(listOf(DigimonSummary(id, name)), next?.let(::PageKey))

    private class Request(val criteria: SearchCriteria, val page: PageKey) {
        val result = CompletableDeferred<BrowseResult<BrowsePage>>()
        fun complete(page: BrowsePage) { result.complete(BrowseResult.Success(page)) }
    }

    private class ControlledRepository(private val ignoreCancellation: Boolean = false) : BrowseRepository {
        val requests = mutableListOf<Request>()
        var cancelled = false
        var levelCalls = 0
        var levelResult: BrowseResult<List<String>> = BrowseResult.Success(listOf("Child", "Adult"))
        override suspend fun browse(criteria: SearchCriteria, page: PageKey): BrowseResult<BrowsePage> {
            val request = Request(criteria, page).also(requests::add)
            return try {
                if (ignoreCancellation) withContext(NonCancellable) { request.result.await() }
                else request.result.await()
            } finally { if (!currentCoroutineContext().isActive) cancelled = true }
        }
        override suspend fun levels(): BrowseResult<List<String>> {
            levelCalls++
            return levelResult
        }
    }
}
