package com.getrighhttt.digimoncompose.domain.profile.model

/**
 * Describes profile lookup failures independently of transport errors and UI wording.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
enum class ProfileFailure {
    Unavailable,
    NotFound,
    InvalidResponse,
    RequestRejected,
}
