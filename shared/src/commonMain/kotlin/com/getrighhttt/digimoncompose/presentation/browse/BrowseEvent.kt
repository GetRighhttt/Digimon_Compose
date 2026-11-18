package com.getrighhttt.digimoncompose.presentation.browse

import com.getrighhttt.digimoncompose.presentation.browse.model.BrowseCriteria
import com.getrighhttt.digimoncompose.presentation.browse.model.BrowseError
import com.getrighhttt.digimoncompose.presentation.browse.model.DigimonCard

/**
 * Describes changes the reducer can apply. Request results carry IDs so the reducer
 * can ignore a result after a newer request starts.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
internal sealed interface BrowseEvent {
    data class BeginReplacement(
        val criteria: BrowseCriteria,
        val generation: Long,
        val requestId: Long,
        val debounce: Boolean,
        val keepItems: Boolean,
    ) : BrowseEvent

    data class Executing(
        val generation: Long,
        val requestId: Long,
    ) : BrowseEvent

    data class Replaced(
        val generation: Long,
        val requestId: Long,
        val items: List<DigimonCard>,
        val nextPage: Int?,
    ) : BrowseEvent

    data class ReplacementFailed(
        val generation: Long,
        val requestId: Long,
        val error: BrowseError,
    ) : BrowseEvent

    data class BeginAppend(
        val requestId: Long,
        val page: Int,
    ) : BrowseEvent

    data class Appended(
        val generation: Long,
        val requestId: Long,
        val page: Int,
        val items: List<DigimonCard>,
        val nextPage: Int?,
    ) : BrowseEvent

    data class AppendFailed(
        val generation: Long,
        val requestId: Long,
        val page: Int,
        val error: BrowseError,
    ) : BrowseEvent

    data object LoadingLevels : BrowseEvent

    data class LevelsLoaded(
        val values: List<String>,
    ) : BrowseEvent

    data class LevelsFailed(
        val error: BrowseError,
    ) : BrowseEvent
}
