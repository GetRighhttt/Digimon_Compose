package com.getrighhttt.digimoncompose.data.browse.model

import kotlinx.serialization.Serializable

/**
 * Decodes one stage-options page with its content envelope and pagination metadata.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Serializable
internal data class DapiLevelsDto(
    val content: DapiLevelContentDto,
    val pageable: DapiPageDto,
)
