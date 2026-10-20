package com.getrighhttt.digimoncompose.data.browse.model

import kotlinx.serialization.Serializable

@Serializable
internal data class DapiPageDto(
    val currentPage: Int,
    val elementsOnPage: Int,
    val totalElements: Int,
    val nextPage: String,
)
