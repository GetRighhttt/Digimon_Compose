package com.getrighhttt.digimoncompose.presentation.profile.model

/**
 * Lets the profile choose an error message without reading a transport exception.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
enum class ProfileError {
    Unavailable,
    NotFound,
    InvalidResponse,
    RequestRejected,
}
