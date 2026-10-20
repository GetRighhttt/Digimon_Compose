package com.getrighhttt.digimoncompose.presentation.profile.model

/**
 * Identifies profile failures for UI message mapping without exposing transport errors.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
enum class ProfileError { Unavailable, NotFound, InvalidResponse, RequestRejected }
