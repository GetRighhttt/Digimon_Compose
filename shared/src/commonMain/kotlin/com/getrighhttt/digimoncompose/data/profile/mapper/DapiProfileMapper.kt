package com.getrighhttt.digimoncompose.data.profile.mapper

import com.getrighhttt.digimoncompose.data.browse.model.DapiException
import com.getrighhttt.digimoncompose.data.profile.model.DapiProfileDto
import com.getrighhttt.digimoncompose.domain.profile.model.*
import io.ktor.http.Url

/** Prefer reference-book English; absent English text has an explicit UI fallback. */
internal fun DapiProfileDto.toDomain(requestedId: Int): DigimonProfile {
    if (id != requestedId || id <= 0 || name.isBlank()) throw DapiException.InvalidResponse()
    val english = descriptions.filter { it.language.startsWith("en", ignoreCase = true) && it.description.isNotBlank() }
    return DigimonProfile(
        id, name, images.firstNotNullOfOrNull {
            try { it.href.takeIf { value -> Url(value).protocol.name == "https" } } catch (_: Exception) { null }
        },
        levels.map { it.level }.filter(String::isNotBlank).distinct(),
        types.map { it.type }.filter(String::isNotBlank).distinct(),
        attributes.map { it.attribute }.filter(String::isNotBlank).distinct(),
        (english.firstOrNull { it.origin == "reference_book" } ?: english.firstOrNull())?.description,
        releaseDate?.takeIf(String::isNotBlank), xAntibody,
        skills.filter { it.skill.isNotBlank() }.map { DigimonSkill(it.skill, it.description) },
        priorEvolutions?.distinctBy { it.id }?.size, nextEvolutions?.distinctBy { it.id }?.size,
    )
}
