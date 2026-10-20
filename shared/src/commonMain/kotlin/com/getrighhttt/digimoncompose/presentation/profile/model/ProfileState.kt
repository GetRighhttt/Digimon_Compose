package com.getrighhttt.digimoncompose.presentation.profile.model

/**
 * Tracks the selected profile, active request, accepted content, and any loading
 * failure.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
data class ProfileState(
    val id: Int? = null,
    val requestId: Long? = null,

    val loading: Boolean = false,

    val content: ProfileContent? = null,
    val error: ProfileError? = null,
)
