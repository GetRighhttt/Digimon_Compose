package com.getrighhttt.digimoncompose.presentation.profile.model

/**
 * Describes what the UI asks the profile store to do: open, retry, or close a profile.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
sealed interface ProfileIntent {
    data class Open(
        val id: Int,
    ) : ProfileIntent

    data object Retry : ProfileIntent

    data object Closed : ProfileIntent
}
