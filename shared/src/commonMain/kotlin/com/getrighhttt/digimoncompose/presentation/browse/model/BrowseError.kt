package com.getrighhttt.digimoncompose.presentation.browse.model

/**
 * Lets the catalog choose its own error message without reading an exception.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
enum class BrowseError {
    Unavailable,
    InvalidPage,
    InvalidResponse,
    RequestRejected,
}
