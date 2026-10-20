package com.getrighhttt.digimoncompose.presentation.profile

import com.getrighhttt.digimoncompose.presentation.profile.model.*

/**
 * Carries operation results and request identities through the same loop as profile intents.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
internal sealed interface ProfileEvent {
    data class Loading(val id: Int, val requestId: Long) : ProfileEvent
    data class Loaded(val id: Int, val requestId: Long, val content: ProfileContent) : ProfileEvent
    data class Failed(val id: Int, val requestId: Long, val error: ProfileError) : ProfileEvent
    data object Closed : ProfileEvent
}
