package com.getrighhttt.digimoncompose.data.browse.model

import kotlinx.serialization.Serializable

/**
 * Decodes a source stage identifier and its canonical name for filter options.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Serializable
internal data class DapiLevelDto(val id: Int, val name: String)
