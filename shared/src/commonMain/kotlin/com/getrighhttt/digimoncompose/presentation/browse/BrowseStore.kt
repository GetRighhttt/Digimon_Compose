package com.getrighhttt.digimoncompose.presentation.browse

import com.getrighhttt.digimoncompose.domain.browse.model.*
import com.getrighhttt.digimoncompose.domain.browse.repository.BrowseRepository
import com.getrighhttt.digimoncompose.presentation.browse.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * One loop owns state. Child operations report results with request identities.
 * The host owns this store; close cancels its child scope, never the injected parent.
 */
class BrowseStore(private val repository: BrowseRepository, parentScope: CoroutineScope) {
    private val storeJob = SupervisorJob(parentScope.coroutineContext[Job])
    private val scope = CoroutineScope(parentScope.coroutineContext + storeJob)
    private val messages = Channel<Message>(Channel.UNLIMITED)
    private val mutableState = MutableStateFlow(BrowseState())
    val state = mutableState.asStateFlow()
    private var replacementJob: Job? = null
    private var appendJob: Job? = null
    private var levelJob: Job? = null
    private var nextRequestId = 0L
    private var started = false

    init {
        scope.launch {
            for (message in messages) when (message) {
                is Message.Intent -> handle(message.value)
                is Message.Event -> apply(message.value)
            }
        }
    }

    fun dispatch(intent: BrowseIntent) { messages.trySend(Message.Intent(intent)) }
    fun close() { messages.close(); storeJob.cancel() }

    private fun apply(event: BrowseEvent) { mutableState.value = reduceBrowse(state.value, event) }
    private fun report(event: BrowseEvent) { messages.trySend(Message.Event(event)) }

    private fun handle(intent: BrowseIntent) {
        val current = state.value
        when (intent) {
            BrowseIntent.Started -> if (!started) {
                started = true
                loadLevels()
                if (current.load == BrowseLoadState.Idle) replace(current.criteria)
            }
            is BrowseIntent.QueryChanged -> if (intent.text != current.criteria.query) {
                replace(current.criteria.copy(query = intent.text), debounce = intent.text.isNotBlank())
            }
            is BrowseIntent.LevelChanged -> if (intent.level != current.criteria.level) {
                replace(current.criteria.copy(level = intent.level))
            }
            BrowseIntent.Refresh -> replace(current.criteria, keepItems = true)
            BrowseIntent.RetryReplacement -> if (current.load is BrowseLoadState.Failed) {
                replace(current.criteria, keepItems = true)
            }
            BrowseIntent.LoadNextPage -> if (current.load == BrowseLoadState.Ready && current.append == AppendState.Idle) append()
            BrowseIntent.RetryAppend -> if (current.load == BrowseLoadState.Ready && current.append is AppendState.Failed) append()
            BrowseIntent.RetryLevels -> if (current.levels is LevelOptionsState.Failed) loadLevels()
        }
    }

    private fun replace(criteria: BrowseCriteria, debounce: Boolean = false, keepItems: Boolean = false) {
        replacementJob?.cancel()
        appendJob?.cancel()
        val generation = state.value.generation + 1
        val requestId = ++nextRequestId
        apply(BrowseEvent.BeginReplacement(criteria, generation, requestId, debounce, keepItems))
        replacementJob = scope.launch {
            if (debounce) delay(300)
            report(BrowseEvent.Executing(generation, requestId))
            when (val result = safely { repository.browse(criteria.toDomain(), PageKey(0)) }) {
                is BrowseResult.Success -> report(BrowseEvent.Replaced(generation, requestId,
                    result.value.items.map { it.toCard() }, result.value.nextPage?.index))
                is BrowseResult.Failure -> report(BrowseEvent.ReplacementFailed(generation, requestId, result.reason.toUiError()))
            }
        }
    }

    private fun append() {
        val current = state.value
        val page = current.nextPage ?: return
        val requestId = ++nextRequestId
        apply(BrowseEvent.BeginAppend(requestId, page))
        appendJob = scope.launch {
            when (val result = safely { repository.browse(current.criteria.toDomain(), PageKey(page)) }) {
                is BrowseResult.Success -> report(BrowseEvent.Appended(current.generation, requestId, page,
                    result.value.items.map { it.toCard() }, result.value.nextPage?.index))
                is BrowseResult.Failure -> report(BrowseEvent.AppendFailed(current.generation, requestId, page, result.reason.toUiError()))
            }
        }
    }

    private fun loadLevels() {
        levelJob?.cancel()
        apply(BrowseEvent.LoadingLevels)
        levelJob = scope.launch {
            when (val result = safely { repository.levels() }) {
                is BrowseResult.Success -> report(BrowseEvent.LevelsLoaded(result.value))
                is BrowseResult.Failure -> report(BrowseEvent.LevelsFailed(result.reason.toUiError()))
            }
        }
    }

    private suspend fun <T> safely(operation: suspend () -> BrowseResult<T>): BrowseResult<T> = try {
        operation()
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (_: Exception) {
        BrowseResult.Failure(BrowseFailure.Unavailable)
    }

    private fun BrowseCriteria.toDomain() = SearchCriteria(query.trim().lowercase(), level)
    private sealed interface Message {
        data class Intent(val value: BrowseIntent) : Message
        data class Event(val value: BrowseEvent) : Message
    }
}
