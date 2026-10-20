package com.getrighhttt.digimoncompose.domain.browse.model

/**
 * Describes catalog failures independently of HTTP exceptions and UI wording.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
enum class BrowseFailure { Unavailable, InvalidPage, InvalidResponse, RequestRejected }
