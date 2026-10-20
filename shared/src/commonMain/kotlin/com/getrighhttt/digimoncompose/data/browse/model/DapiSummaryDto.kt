package com.getrighhttt.digimoncompose.data.browse.model

import kotlinx.serialization.Serializable

@Serializable
internal data class DapiSummaryDto(val id: Int, val name: String, val image: String? = null)
