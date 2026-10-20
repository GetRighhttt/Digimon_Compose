package com.getrighhttt.digimoncompose.presentation.profile

import com.getrighhttt.digimoncompose.domain.profile.model.*
import com.getrighhttt.digimoncompose.domain.profile.repository.ProfileRepository
import com.getrighhttt.digimoncompose.presentation.profile.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * One loop owns profile state. The session closes this child scope with its Browse owner.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
class ProfileStore(private val repository: ProfileRepository, parentScope: CoroutineScope) {
    private val job = SupervisorJob(parentScope.coroutineContext[Job])
    private val scope = CoroutineScope(parentScope.coroutineContext + job)
    private val messages = Channel<Message>(Channel.UNLIMITED)
    private val mutableState = MutableStateFlow(ProfileState())
    val state = mutableState.asStateFlow()
    private var request: Job? = null
    private var sequence = 0L
    init { scope.launch {
        for (message in messages) when (message) {
            is Message.Event -> apply(message.value)
            is Message.Intent -> when (val intent = message.value) {
                is ProfileIntent.Open -> if (state.value.id != intent.id) load(intent.id)
                ProfileIntent.Retry -> state.value.id?.let { if (state.value.error != null) load(it) }
                ProfileIntent.Closed -> { request?.cancel(); apply(ProfileEvent.Closed) }
            }
        }
    } }
    fun dispatch(intent: ProfileIntent) { messages.trySend(Message.Intent(intent)) }
    fun close() { messages.close(); job.cancel() }
    private fun apply(event: ProfileEvent) { mutableState.value = reduceProfile(state.value, event) }
    private fun load(id: Int) {
        request?.cancel()
        val requestId = ++sequence
        apply(ProfileEvent.Loading(id, requestId))
        request = scope.launch {
            val result = try { repository.profile(id)
            } catch (cancelled: CancellationException) { throw cancelled
            } catch (_: Exception) { ProfileResult.Failure(ProfileFailure.Unavailable) }
            val event = when (result) {
                is ProfileResult.Success -> ProfileEvent.Loaded(id, requestId, result.profile.toUi())
                is ProfileResult.Failure -> ProfileEvent.Failed(id, requestId, result.reason.toUi())
            }
            messages.trySend(Message.Event(event))
        }
    }
    private sealed interface Message {
        data class Intent(val value: ProfileIntent) : Message
        data class Event(val value: ProfileEvent) : Message
    }
}
