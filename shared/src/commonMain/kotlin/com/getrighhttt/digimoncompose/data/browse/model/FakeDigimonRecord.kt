package com.getrighhttt.digimoncompose.data.browse.model

/**
 * Keeps stage data for fake catalog filtering, even though catalog cards do not show it.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
internal data class FakeDigimonRecord(
    val id: Int,
    val name: String,
    val level: String,
)
