package com.getrighhttt.digimoncompose.presentation.profile

import com.getrighhttt.digimoncompose.presentation.profile.model.ProfileContent
import com.getrighhttt.digimoncompose.presentation.profile.model.ProfileError

/**
 * Carries load results to the reducer. Profile and request IDs identify the attempt.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
internal sealed interface ProfileEvent {
    data class Loading(
        val id: Int,
        val requestId: Long,
    ) : ProfileEvent

    data class Loaded(
        val id: Int,
        val requestId: Long,
        val content: ProfileContent,
    ) : ProfileEvent

    data class Failed(
        val id: Int,
        val requestId: Long,
        val error: ProfileError,
    ) : ProfileEvent

    data object Closed : ProfileEvent
}
