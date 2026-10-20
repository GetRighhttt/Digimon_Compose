package com.getrighhttt.digimoncompose.presentation.profile

import com.getrighhttt.digimoncompose.domain.profile.model.*
import com.getrighhttt.digimoncompose.domain.profile.repository.ProfileRepository
import com.getrighhttt.digimoncompose.presentation.profile.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import kotlin.test.*

/**
 * Checks profile request ordering, retry, cancellation, and reducer rejection of obsolete completions.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProfileStoreTest {
    @Test fun repeatedOpenLoadsOnceAndMapsContent() = runTest {
        var calls = 0
        val store = ProfileStore(object : ProfileRepository {
            override suspend fun profile(id: Int): ProfileResult { calls++; return ProfileResult.Success(sampleProfile(id)) }
        }, backgroundScope)
        store.dispatch(ProfileIntent.Open(1)); store.dispatch(ProfileIntent.Open(1)); runCurrent()
        assertEquals(1, calls)
        assertEquals("Digimon 1", store.state.value.content?.name)
        assertFalse(store.state.value.loading)
        store.close()
    }

    @Test fun retryUsesTheSelectedIdentifierAndClearsError() = runTest {
        var calls = 0
        val store = ProfileStore(object : ProfileRepository {
            override suspend fun profile(id: Int): ProfileResult {
                calls++
                return if (calls == 1) ProfileResult.Failure(ProfileFailure.Unavailable) else ProfileResult.Success(sampleProfile(id))
            }
        }, backgroundScope)
        store.dispatch(ProfileIntent.Open(7)); runCurrent()
        assertEquals(ProfileError.Unavailable, store.state.value.error)
        store.dispatch(ProfileIntent.Retry); runCurrent()
        assertEquals(7, store.state.value.content?.id)
        assertNull(store.state.value.error)
        store.close()
    }

    @Test fun newerProfileWinsEvenWhenOldRepositoryIgnoresCancellation() = runTest {
        val old = CompletableDeferred<ProfileResult>()
        val store = ProfileStore(object : ProfileRepository {
            override suspend fun profile(id: Int): ProfileResult = if (id == 1) {
                withContext(NonCancellable) { old.await() }
            } else ProfileResult.Success(sampleProfile(id))
        }, backgroundScope)
        store.dispatch(ProfileIntent.Open(1)); runCurrent()
        store.dispatch(ProfileIntent.Open(2)); runCurrent()
        old.complete(ProfileResult.Success(sampleProfile(1))); runCurrent()
        assertEquals(2, store.state.value.content?.id)
        store.close()
    }

    @Test fun closingSelectionCancelsPendingProfileAndKeepsParentAlive() = runTest {
        var cancelled = false
        val store = ProfileStore(object : ProfileRepository {
            override suspend fun profile(id: Int): ProfileResult = try { awaitCancellation()
            } finally { cancelled = true }
        }, backgroundScope)
        store.dispatch(ProfileIntent.Open(1)); runCurrent()
        store.dispatch(ProfileIntent.Closed); runCurrent()
        assertTrue(cancelled)
        assertEquals(ProfileState(), store.state.value)
        assertTrue(backgroundScope.isActive)
        store.close()
    }

    @Test fun reducerIgnoresLateCompletionsAfterCloseOrReplacement() {
        val old = ProfileState(id = 1, requestId = 1, loading = true)
        val newer = reduceProfile(old, ProfileEvent.Loading(2, 2))
        assertEquals(newer, reduceProfile(newer, ProfileEvent.Failed(1, 1, ProfileError.Unavailable)))
        val closed = reduceProfile(newer, ProfileEvent.Closed)
        assertEquals(closed, reduceProfile(closed, ProfileEvent.Loaded(2, 2, sampleProfile(2).toUi())))
    }

    private fun sampleProfile(id: Int) = DigimonProfile(id, "Digimon $id", null, emptyList(), emptyList(), emptyList(),
        null, null, false, emptyList(), 0, 0)
}
