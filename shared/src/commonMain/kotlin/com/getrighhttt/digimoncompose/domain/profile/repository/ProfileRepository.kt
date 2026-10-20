package com.getrighhttt.digimoncompose.domain.profile.repository

import com.getrighhttt.digimoncompose.domain.profile.model.ProfileResult

/**
 * Defines profile lookup by source identifier without exposing transport details.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
interface ProfileRepository { suspend fun profile(id: Int): ProfileResult }
