package com.getrighhttt.digimoncompose.presentation.profile

import com.getrighhttt.digimoncompose.domain.profile.model.DigimonProfile
import com.getrighhttt.digimoncompose.domain.profile.model.ProfileFailure
import com.getrighhttt.digimoncompose.presentation.profile.model.ProfileContent
import com.getrighhttt.digimoncompose.presentation.profile.model.ProfileError
import com.getrighhttt.digimoncompose.presentation.profile.model.ProfileSkill

/**
 * Maps domain profile content and failures into presentation-owned values.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
internal fun DigimonProfile.toUi() =
    ProfileContent(
        id = id,
        name = name,
        image = image,
        levels = levels.toList(),
        types = types.toList(),
        attributes = attributes.toList(),
        description = description,
        debut = debut,
        xAntibody = xAntibody,
        skills = skills.map { ProfileSkill(name = it.name, description = it.description) },
        priorCount = priorCount,
        nextCount = nextCount,
    )

internal fun ProfileFailure.toUi() =
    when (this) {
        ProfileFailure.Unavailable -> ProfileError.Unavailable
        ProfileFailure.NotFound -> ProfileError.NotFound
        ProfileFailure.InvalidResponse -> ProfileError.InvalidResponse
        ProfileFailure.RequestRejected -> ProfileError.RequestRejected
    }
