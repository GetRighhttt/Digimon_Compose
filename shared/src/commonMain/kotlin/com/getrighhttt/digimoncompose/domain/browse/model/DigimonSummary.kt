package com.getrighhttt.digimoncompose.domain.browse.model

/**
 * Holds a catalog entry's validated ID and name, plus artwork when available.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
data class DigimonSummary(
    val id: Int,
    val name: String,
    val image: String? = null,
)
