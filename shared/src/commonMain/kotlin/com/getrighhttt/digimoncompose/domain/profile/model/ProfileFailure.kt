package com.getrighhttt.digimoncompose.domain.profile.model

/**
 * Describes what went wrong loading a profile without choosing the UI message.
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
