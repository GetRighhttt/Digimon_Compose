package com.getrighhttt.digimoncompose.presentation.profile

import com.getrighhttt.digimoncompose.presentation.profile.model.*

/**
 * Closing or replacing a profile invalidates any earlier completion.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
internal fun reduceProfile(state: ProfileState, event: ProfileEvent): ProfileState = when (event) {
    is ProfileEvent.Loading -> ProfileState(event.id, event.requestId, loading = true)
    is ProfileEvent.Loaded -> if (state.id == event.id && state.requestId == event.requestId) {
        state.copy(requestId = null, loading = false, content = event.content, error = null)
    } else state
    is ProfileEvent.Failed -> if (state.id == event.id && state.requestId == event.requestId) {
        state.copy(requestId = null, loading = false, error = event.error)
    } else state
    ProfileEvent.Closed -> ProfileState()
}
