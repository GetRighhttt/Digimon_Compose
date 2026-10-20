package com.getrighhttt.digimoncompose.presentation.browse.model

/**
 * Keeps user-entered search text and the selected stage together in presentation state.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
data class BrowseCriteria(val query: String = "", val level: String? = null)
