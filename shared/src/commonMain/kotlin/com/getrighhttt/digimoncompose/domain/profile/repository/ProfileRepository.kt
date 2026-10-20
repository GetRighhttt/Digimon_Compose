package com.getrighhttt.digimoncompose.domain.profile.repository

import com.getrighhttt.digimoncompose.domain.profile.model.ProfileResult

interface ProfileRepository { suspend fun profile(id: Int): ProfileResult }
