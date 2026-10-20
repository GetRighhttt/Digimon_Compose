package com.getrighhttt.digimoncompose.presentation.profile.model

data class ProfileContent(
    val id: Int, val name: String, val image: String?,
    val levels: List<String>, val types: List<String>, val attributes: List<String>,
    val description: String?, val debut: String?, val xAntibody: Boolean?,
    val skills: List<ProfileSkill>, val priorCount: Int?, val nextCount: Int?,
)
