package com.getrighhttt.digimoncompose.data.profile.model

import kotlinx.serialization.Serializable

/**
 * Decodes profile text with its language and origin so mapping can choose an English description.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Serializable
internal data class DapiDescriptionDto(val language: String, val description: String, val origin: String = "")
