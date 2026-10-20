package com.getrighhttt.digimoncompose.presentation.browse.model

/**
 * Distinguishes initial loading, debounce, refresh, ready content, and replacement
 * failure.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
sealed interface BrowseLoadState {
    data object Idle : BrowseLoadState

    data object Debouncing : BrowseLoadState

    data object Loading : BrowseLoadState

    data object Refreshing : BrowseLoadState

    data object Ready : BrowseLoadState

    data class Failed(
        val error: BrowseError,
    ) : BrowseLoadState
}
