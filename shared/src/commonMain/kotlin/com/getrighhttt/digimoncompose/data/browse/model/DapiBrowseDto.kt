package com.getrighhttt.digimoncompose.data.browse.model

import kotlinx.serialization.Serializable

/**
 * DAPI omits content for some successful zero-result searches.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Serializable
internal data class DapiBrowseDto(
    val content: List<DapiSummaryDto>? = null,
    val pageable: DapiPageDto,
)
