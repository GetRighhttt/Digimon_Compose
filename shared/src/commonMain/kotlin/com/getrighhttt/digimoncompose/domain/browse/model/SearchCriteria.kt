package com.getrighhttt.digimoncompose.domain.browse.model

/**
 * Values used to request a catalog page, independent of the renderer.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
data class SearchCriteria(
    val name: String = "",
    val level: String? = null,
)
