package com.getrighhttt.digimoncompose.data.profile.model

import kotlinx.serialization.Serializable

/**
 * Reads the API message used to tell a missing profile from other request errors.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Serializable
internal data class DapiErrorDto(
    val message: String = "",
)
