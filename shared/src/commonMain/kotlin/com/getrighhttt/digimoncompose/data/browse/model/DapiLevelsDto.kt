package com.getrighhttt.digimoncompose.data.browse.model

import kotlinx.serialization.Serializable

/**
 * Matches the stage list response, including its page information.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Serializable
internal data class DapiLevelsDto(
    val content: DapiLevelContentDto,
    val pageable: DapiPageDto,
)
