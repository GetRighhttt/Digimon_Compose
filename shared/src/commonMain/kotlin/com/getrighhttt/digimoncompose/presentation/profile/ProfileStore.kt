package com.getrighhttt.digimoncompose.presentation.profile

import com.getrighhttt.digimoncompose.domain.profile.model.ProfileFailure
import com.getrighhttt.digimoncompose.domain.profile.model.ProfileResult
import com.getrighhttt.digimoncompose.domain.profile.repository.ProfileRepository
import com.getrighhttt.digimoncompose.presentation.profile.model.ProfileIntent
import com.getrighhttt.digimoncompose.presentation.profile.model.ProfileState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Serializes profile actions and results within the session's child scope.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
class ProfileStore(
    private val repository: ProfileRepository,
    parentScope: CoroutineScope,
) {
    private val job = SupervisorJob(parentScope.coroutineContext[Job])
    private val scope = CoroutineScope(parentScope.coroutineContext + job)
    private val messages = Channel<Message>(Channel.UNLIMITED)
    private val mutableState = MutableStateFlow(ProfileState())
    val state = mutableState.asStateFlow()
    private var request: Job? = null
    private var sequence = 0L

    init {
        scope.launch {
            for (message in messages) {
                when (message) {
                    is Message.Event -> apply(message.value)
                    is Message.Intent -> handle(message.value)
                }
            }
        }
    }

    fun dispatch(intent: ProfileIntent) {
        messages.trySend(Message.Intent(intent))
    }

    fun close() {
        messages.close()
        job.cancel()
    }

    private fun apply(event: ProfileEvent) {
        mutableState.value = reduceProfile(state.value, event)
    }

    private fun handle(intent: ProfileIntent) {
        val current = state.value
        when (intent) {
            is ProfileIntent.Open -> {
                if (current.id != intent.id) load(intent.id)
            }

            ProfileIntent.Retry -> {
                val id = current.id
                if (id != null && current.error != null) load(id)
            }

            ProfileIntent.Closed -> {
                request?.cancel()
                apply(ProfileEvent.Closed)
            }
        }
    }

    private fun load(id: Int) {
        request?.cancel()
        val requestId = ++sequence
        apply(ProfileEvent.Loading(id, requestId))
        request =
            scope.launch {
                val result =
                    try {
                        repository.profile(id)
                    } catch (cancelled: CancellationException) {
                        throw cancelled
                    } catch (_: Exception) {
                        ProfileResult.Failure(ProfileFailure.Unavailable)
                    }
                val event =
                    when (result) {
                        is ProfileResult.Success -> {
                            ProfileEvent.Loaded(id, requestId, result.profile.toUi())
                        }

                        is ProfileResult.Failure -> {
                            ProfileEvent.Failed(id, requestId, result.reason.toUi())
                        }
                    }
                messages.trySend(Message.Event(event))
            }
    }

    private sealed interface Message {
        data class Intent(
            val value: ProfileIntent,
        ) : Message

        data class Event(
            val value: ProfileEvent,
        ) : Message
    }
}
