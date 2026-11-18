package com.getrighhttt.digimoncompose.data.browse.model

import kotlinx.serialization.Serializable

/**
 * Holds the API's page counts and next link so the mapper can check both.
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
