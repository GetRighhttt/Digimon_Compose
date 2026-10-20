package com.getrighhttt.digimoncompose.presentation.profile.model

/**
 * Defines profile selection, retry, and closing actions accepted by the profile store.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
sealed interface ProfileIntent {
    data class Open(val id: Int) : ProfileIntent
    data object Retry : ProfileIntent
    data object Closed : ProfileIntent
}
