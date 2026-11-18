package com.getrighhttt.digimoncompose.data.browse.model

import kotlinx.serialization.Serializable

/**
 * Matches DAPI's fields object, which holds a page of stage filters.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Serializable
internal data class DapiLevelContentDto(
    val fields: List<DapiLevelDto>,
)
