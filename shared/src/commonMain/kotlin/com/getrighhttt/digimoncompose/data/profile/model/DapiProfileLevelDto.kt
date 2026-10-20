package com.getrighhttt.digimoncompose.data.profile.model

import kotlinx.serialization.Serializable

/**
 * Decodes a stage label from a profile classification entry.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Serializable
internal data class DapiProfileLevelDto(val level: String)
