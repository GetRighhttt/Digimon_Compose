package com.getrighhttt.digimoncompose.data.profile.model

import kotlinx.serialization.Serializable

@Serializable
internal data class DapiSkillDto(val skill: String, val description: String = "")
