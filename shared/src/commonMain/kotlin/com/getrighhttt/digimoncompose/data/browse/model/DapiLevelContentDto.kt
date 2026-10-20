package com.getrighhttt.digimoncompose.data.browse.model

import kotlinx.serialization.Serializable

/**
 * Decodes the fields envelope containing one page of DAPI stage options.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Serializable
internal data class DapiLevelContentDto(val fields: List<DapiLevelDto>)
