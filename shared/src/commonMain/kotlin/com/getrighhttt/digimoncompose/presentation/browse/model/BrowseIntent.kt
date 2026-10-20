package com.getrighhttt.digimoncompose.presentation.browse.model

/**
 * Defines user and lifecycle actions accepted by the serialized Browse store.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
sealed interface BrowseIntent {
    data object Started : BrowseIntent
    data class QueryChanged(val text: String) : BrowseIntent
    data class LevelChanged(val level: String?) : BrowseIntent
    data object Refresh : BrowseIntent
    data object RetryReplacement : BrowseIntent
    data object LoadNextPage : BrowseIntent
    data object RetryAppend : BrowseIntent
    data object RetryLevels : BrowseIntent
}
