package com.getrighhttt.digimoncompose.presentation.browse.model

/**
 * Separates a fresh search from a refresh so the UI can keep showing existing cards.
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
