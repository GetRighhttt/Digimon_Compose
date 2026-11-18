package com.getrighhttt.digimoncompose.domain.browse.model

/**
 * Describes what went wrong without choosing what the UI says about it.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
enum class BrowseFailure {
    Unavailable,
    InvalidPage,
    InvalidResponse,
    RequestRejected,
}
