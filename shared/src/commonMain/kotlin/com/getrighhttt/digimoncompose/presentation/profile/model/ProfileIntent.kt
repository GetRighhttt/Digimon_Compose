package com.getrighhttt.digimoncompose.presentation.profile.model

sealed interface ProfileIntent {
    data class Open(val id: Int) : ProfileIntent
    data object Retry : ProfileIntent
    data object Closed : ProfileIntent
}
