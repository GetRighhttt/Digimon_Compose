package com.getrighhttt.digimoncompose.domain.profile.model

/**
 * Returns either an accepted profile or a typed failure to the presentation boundary.
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
