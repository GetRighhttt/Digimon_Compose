package com.getrighhttt.digimoncompose.domain.browse.model

/**
 * Contains accepted catalog summaries and the continuation for the criteria that
 * produced them.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
data class BrowsePage(
    val items: List<DigimonSummary>,
    val nextPage: PageKey?,
)
