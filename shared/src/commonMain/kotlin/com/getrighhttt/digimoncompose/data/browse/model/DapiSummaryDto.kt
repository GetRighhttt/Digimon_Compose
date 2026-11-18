package com.getrighhttt.digimoncompose.data.browse.model

import kotlinx.serialization.Serializable

/**
 * Matches the catalog response: ID, name, and optional artwork.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Serializable
internal data class DapiSummaryDto(
    val id: Int,
    val name: String,
    val image: String? = null,
)
