package com.getrighhttt.digimoncompose.data.profile.model

import kotlinx.serialization.Serializable

/**
 * Decodes the API error message used to distinguish the observed profile-not-found response.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Serializable
internal data class DapiErrorDto(val message: String = "")
