package com.getrighhttt.digimoncompose.data.profile.mapper

import com.getrighhttt.digimoncompose.data.browse.model.DapiException
import com.getrighhttt.digimoncompose.data.profile.model.DapiProfileDto
import com.getrighhttt.digimoncompose.domain.profile.model.DigimonProfile
import com.getrighhttt.digimoncompose.domain.profile.model.DigimonSkill
import io.ktor.http.Url

/**
 * Maps validated profiles and prefers reference-book English descriptions.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
internal fun DapiProfileDto.toDomain(requestedId: Int): DigimonProfile {
    if (id != requestedId || id <= 0 || name.isBlank()) throw DapiException.InvalidResponse()
    val english =
        descriptions.filter {
            it.language.startsWith("en", ignoreCase = true) && it.description.isNotBlank()
        }
    val description = english.firstOrNull { it.origin == "reference_book" } ?: english.firstOrNull()

    return DigimonProfile(
        id = id,
        name = name,
        image = images.firstNotNullOfOrNull { image -> image.href.takeIf(::isHttpsImage) },
        levels = levels.map { it.level }.filter(String::isNotBlank).distinct(),
        types = types.map { it.type }.filter(String::isNotBlank).distinct(),
        attributes = attributes.map { it.attribute }.filter(String::isNotBlank).distinct(),
        description = description?.description,
        debut = releaseDate?.takeIf(String::isNotBlank),
        xAntibody = xAntibody,
        skills =
            skills
                .filter { it.skill.isNotBlank() }
                .map {
                    DigimonSkill(name = it.skill, description = it.description)
                },
        priorCount = priorEvolutions?.distinctBy { it.id }?.size,
        nextCount = nextEvolutions?.distinctBy { it.id }?.size,
    )
}

private fun isHttpsImage(value: String): Boolean =
    try {
        Url(value).protocol.name == "https"
    } catch (_: Exception) {
        false
    }
