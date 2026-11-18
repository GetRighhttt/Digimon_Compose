package com.getrighhttt.digimoncompose.domain.profile.model

/**
 * Returns profile data or a failure the screen can handle.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
sealed interface ProfileResult {
    data class Success(
        val profile: DigimonProfile,
    ) : ProfileResult

    data class Failure(
        val reason: ProfileFailure,
    ) : ProfileResult
}
