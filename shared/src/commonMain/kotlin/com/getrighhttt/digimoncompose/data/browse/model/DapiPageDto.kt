package com.getrighhttt.digimoncompose.data.browse.model

import kotlinx.serialization.Serializable

/**
 * Carries source pagination metadata for count validation and continuation checks.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Serializable
internal data class DapiPageDto(
    val currentPage: Int,
    val elementsOnPage: Int,
    val totalElements: Int,
    val nextPage: String,
)
