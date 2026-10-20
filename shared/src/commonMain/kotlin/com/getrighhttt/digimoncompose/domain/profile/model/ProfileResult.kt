package com.getrighhttt.digimoncompose.domain.profile.model

sealed interface ProfileResult {
    data class Success(val profile: DigimonProfile) : ProfileResult
    data class Failure(val reason: ProfileFailure) : ProfileResult
}
