package com.getrighhttt.digimoncompose.data.profile.model

import kotlinx.serialization.Serializable

/**
 * Decodes profile metadata with defaults for optional collections and nullable unknown values.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Serializable
internal data class DapiProfileDto(val id: Int, val name: String,
    val images: List<DapiImageDto> = emptyList(),
    val levels: List<DapiProfileLevelDto> = emptyList(),
    val types: List<DapiProfileTypeDto> = emptyList(),
    val attributes: List<DapiProfileAttributeDto> = emptyList(),
    val descriptions: List<DapiDescriptionDto> = emptyList(),
    val skills: List<DapiSkillDto> = emptyList(),
    val priorEvolutions: List<DapiEvolutionDto>? = null,
    val nextEvolutions: List<DapiEvolutionDto>? = null,
    val releaseDate: String? = null,
    val xAntibody: Boolean? = null)
