package com.getrighhttt.digimoncompose.data.profile.repository

import com.getrighhttt.digimoncompose.data.browse.model.DapiException
import com.getrighhttt.digimoncompose.data.browse.source.DapiSource
import com.getrighhttt.digimoncompose.data.profile.mapper.toDomain
import com.getrighhttt.digimoncompose.domain.profile.model.*
import com.getrighhttt.digimoncompose.domain.profile.repository.ProfileRepository
import kotlinx.coroutines.CancellationException

/**
 * Loads validated profiles and translates data failures into typed domain results while propagating cancellation.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
internal class DapiProfileRepository(private val source: DapiSource) : ProfileRepository {
    override suspend fun profile(id: Int): ProfileResult {
        if (id <= 0) return ProfileResult.Failure(ProfileFailure.NotFound)
        return try {
            ProfileResult.Success(source.profile(id).toDomain(id))
        } catch (cancelled: CancellationException) { throw cancelled
        } catch (error: DapiException) {
            ProfileResult.Failure(when (error) {
                is DapiException.NotFound -> ProfileFailure.NotFound
                is DapiException.InvalidResponse -> ProfileFailure.InvalidResponse
                is DapiException.Unavailable -> ProfileFailure.Unavailable
                is DapiException.Http -> if (error.status == 408 || error.status == 429 || error.status >= 500) {
                    ProfileFailure.Unavailable
                } else ProfileFailure.RequestRejected
            })
        }
    }
}
