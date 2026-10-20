package com.getrighhttt.digimoncompose.data.profile.model

import kotlinx.serialization.Serializable

@Serializable
internal data class DapiDescriptionDto(val language: String, val description: String, val origin: String = "")
