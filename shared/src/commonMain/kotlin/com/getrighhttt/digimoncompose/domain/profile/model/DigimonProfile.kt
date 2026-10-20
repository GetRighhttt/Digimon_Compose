package com.getrighhttt.digimoncompose.domain.profile.model

/**
 * Accepted profile metadata. Null values mean the source did not supply a value.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
data class DigimonProfile(
    val id: Int,
    val name: String,
    val image: String?,

    val levels: List<String>,
    val types: List<String>,
    val attributes: List<String>,

    val description: String?,
    val debut: String?,
    val xAntibody: Boolean?,

    val skills: List<DigimonSkill>,

    val priorCount: Int?,
    val nextCount: Int?,
)
