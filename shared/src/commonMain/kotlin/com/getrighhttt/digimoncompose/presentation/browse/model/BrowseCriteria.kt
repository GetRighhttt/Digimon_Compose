package com.getrighhttt.digimoncompose.presentation.browse.model

/**
 * Keeps search text as entered and records the selected stage. Requests normalize the text.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
data class BrowseCriteria(
    val query: String = "",
    val level: String? = null,
)
