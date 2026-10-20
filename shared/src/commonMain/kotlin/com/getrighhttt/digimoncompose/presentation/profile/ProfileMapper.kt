package com.getrighhttt.digimoncompose.presentation.profile

import com.getrighhttt.digimoncompose.domain.profile.model.*
import com.getrighhttt.digimoncompose.presentation.profile.model.*

internal fun DigimonProfile.toUi() = ProfileContent(id, name, image, levels.toList(), types.toList(), attributes.toList(),
    description, debut, xAntibody, skills.map { ProfileSkill(it.name, it.description) }, priorCount, nextCount)
internal fun ProfileFailure.toUi() = when (this) {
    ProfileFailure.Unavailable -> ProfileError.Unavailable
    ProfileFailure.NotFound -> ProfileError.NotFound
    ProfileFailure.InvalidResponse -> ProfileError.InvalidResponse
    ProfileFailure.RequestRejected -> ProfileError.RequestRejected
}
