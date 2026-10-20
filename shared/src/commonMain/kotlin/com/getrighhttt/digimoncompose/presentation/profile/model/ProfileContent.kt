package com.getrighhttt.digimoncompose.presentation.profile.model

/**
 * Contains display-ready profile metadata, skills, and optional evolution relationship counts.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
data class ProfileContent(
    val id: Int, val name: String, val image: String?,
    val levels: List<String>, val types: List<String>, val attributes: List<String>,
    val description: String?, val debut: String?, val xAntibody: Boolean?,
    val skills: List<ProfileSkill>, val priorCount: Int?, val nextCount: Int?,
)
