package com.getrighhttt.digimoncompose.data.profile.model

import kotlinx.serialization.Serializable

/**
 * Decodes a related Digimon identity and name for counting distinct evolution
 * relationships.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Serializable
internal data class DapiEvolutionDto(
    val id: Int,
    val digimon: String,
)
