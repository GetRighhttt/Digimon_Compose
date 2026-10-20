package com.getrighhttt.digimoncompose.domain.browse.model

/**
 * Carries accepted catalog identity, name, and optional artwork without depending on
 * source envelopes.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
data class DigimonSummary(
    val id: Int,
    val name: String,
    val image: String? = null,
)
