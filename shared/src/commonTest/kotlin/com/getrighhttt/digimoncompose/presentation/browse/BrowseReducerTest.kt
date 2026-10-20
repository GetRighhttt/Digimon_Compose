package com.getrighhttt.digimoncompose.presentation.browse

import com.getrighhttt.digimoncompose.presentation.browse.model.*
import kotlin.test.*

/**
 * Checks page merging and request-identity rules without scheduling or network access.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
class BrowseReducerTest {
    @Test fun overlappingPagesUpdateValuesWithoutReorderingExistingCards() {
        val state = BrowseState(generation = 3,
            items = listOf(card(1, "First"), card(2, "Old")),
            load = BrowseLoadState.Ready, nextPage = 1, append = AppendState.Loading(7, 1))
        val next = reduceBrowse(state, BrowseEvent.Appended(3, 7, 1,
            listOf(card(2, "Updated"), card(3, "New")), null))
        assertEquals(listOf("First", "Updated", "New"), next.items.map { it.name })
        assertEquals("Old", state.items[1].name)
        assertNull(next.nextPage)
    }

    @Test fun wrongRequestAndPageCannotCompleteAppend() {
        val state = BrowseState(generation = 3, nextPage = 1, append = AppendState.Loading(7, 1))
        assertEquals(state, reduceBrowse(state, BrowseEvent.Appended(3, 6, 1, listOf(card(1)), null)))
        assertEquals(state, reduceBrowse(state, BrowseEvent.Appended(3, 7, 2, listOf(card(1)), null)))
    }

    @Test fun searchClearsCardsAndAppendBeforeWaitingForDebounce() {
        val state = BrowseState(items = listOf(card(1)), nextPage = 1,
            append = AppendState.Loading(3, 1))
        val next = reduceBrowse(state, BrowseEvent.BeginReplacement(BrowseCriteria("agu"), 2, 4, true, false))
        assertTrue(next.items.isEmpty())
        assertNull(next.nextPage)
        assertEquals(AppendState.Idle, next.append)
        assertEquals(BrowseLoadState.Debouncing, next.load)
    }

    @Test fun emptyPageWithContinuationIsNotTreatedAsFinal() {
        val state = BrowseState(generation = 1, activeReplacement = 2)
        val next = reduceBrowse(state, BrowseEvent.Replaced(1, 2, emptyList(), 1))
        assertEquals(1, next.nextPage)
        assertEquals(BrowseLoadState.Ready, next.load)
    }

    private fun card(id: Int, name: String = "Digimon") = DigimonCard(id, name, null)
}
