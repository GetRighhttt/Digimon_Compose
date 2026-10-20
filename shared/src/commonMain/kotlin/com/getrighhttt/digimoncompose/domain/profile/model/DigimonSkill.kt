package com.getrighhttt.digimoncompose.domain.profile.model

/**
 * Carries a named profile skill and description independently of source and UI models.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
data class DigimonSkill(
    val name: String,
    val description: String,
)
