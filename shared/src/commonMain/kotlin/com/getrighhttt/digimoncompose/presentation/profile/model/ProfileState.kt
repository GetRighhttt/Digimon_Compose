package com.getrighhttt.digimoncompose.presentation.profile.model

data class ProfileState(
    val id: Int? = null, val requestId: Long? = null,
    val loading: Boolean = false, val content: ProfileContent? = null, val error: ProfileError? = null,
)
