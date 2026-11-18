package com.getrighhttt.digimoncompose.presentation.profile.model

/**
 * Holds the visible profile and the ID of the load whose result it can accept.
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
