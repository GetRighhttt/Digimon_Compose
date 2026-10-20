package com.getrighhttt.digimoncompose.data.browse.model

import kotlinx.serialization.Serializable

/**
 * Decodes the identity, name, and optional artwork reference used by catalog summaries.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Serializable
internal data class DapiSummaryDto(val id: Int, val name: String, val image: String? = null)
