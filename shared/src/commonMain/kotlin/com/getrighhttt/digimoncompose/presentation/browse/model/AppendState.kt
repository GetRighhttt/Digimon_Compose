package com.getrighhttt.digimoncompose.presentation.browse.model

/**
 * Tracks pagination progress and retryable failure separately from catalog replacement.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
sealed interface AppendState {
    data object Idle : AppendState

    data class Loading(
        val requestId: Long,
        val page: Int,
    ) : AppendState

    data class Failed(
        val error: BrowseError,
    ) : AppendState
}
