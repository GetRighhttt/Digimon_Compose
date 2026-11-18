package com.getrighhttt.digimoncompose.data.browse.model

import kotlinx.serialization.Serializable

/**
 * Holds a stage ID and the name the API returns for the filter.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Serializable
internal data class DapiLevelDto(
    val id: Int,
    val name: String,
)
