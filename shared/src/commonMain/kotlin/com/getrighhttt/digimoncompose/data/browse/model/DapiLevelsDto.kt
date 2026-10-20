package com.getrighhttt.digimoncompose.data.browse.model

import kotlinx.serialization.Serializable

@Serializable
internal data class DapiLevelsDto(val content: DapiLevelContentDto, val pageable: DapiPageDto)
