package com.getrighhttt.digimoncompose.data.profile.model

import kotlinx.serialization.Serializable

/**
 * Source text with language and origin for choosing a profile description.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Serializable
internal data class DapiDescriptionDto(
    val language: String,
    val description: String,
    val origin: String = "",
)
