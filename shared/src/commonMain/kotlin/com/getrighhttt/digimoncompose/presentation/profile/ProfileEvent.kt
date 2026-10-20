package com.getrighhttt.digimoncompose.presentation.profile

import com.getrighhttt.digimoncompose.presentation.profile.model.*

internal sealed interface ProfileEvent {
    data class Loading(val id: Int, val requestId: Long) : ProfileEvent
    data class Loaded(val id: Int, val requestId: Long, val content: ProfileContent) : ProfileEvent
    data class Failed(val id: Int, val requestId: Long, val error: ProfileError) : ProfileEvent
    data object Closed : ProfileEvent
}
