package com.getrighhttt.digimoncompose.data.profile.model

import kotlinx.serialization.Serializable

/**
 * Holds a related Digimon's ID so the mapper can count distinct evolution links.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Serializable
internal data class DapiEvolutionDto(
    val id: Int,
    val digimon: String,
)
