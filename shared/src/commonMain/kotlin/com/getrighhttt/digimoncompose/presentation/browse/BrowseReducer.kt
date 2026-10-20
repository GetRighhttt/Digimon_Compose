package com.getrighhttt.digimoncompose.presentation.browse

import com.getrighhttt.digimoncompose.presentation.browse.model.AppendState
import com.getrighhttt.digimoncompose.presentation.browse.model.BrowseError
import com.getrighhttt.digimoncompose.presentation.browse.model.BrowseLoadState
import com.getrighhttt.digimoncompose.presentation.browse.model.BrowseState
import com.getrighhttt.digimoncompose.presentation.browse.model.DigimonCard
import com.getrighhttt.digimoncompose.presentation.browse.model.LevelOptionsState

/**
 * Pure transitions. Obsolete responses never change the current generation.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
internal fun reduceBrowse(
    state: BrowseState,
    event: BrowseEvent,
): BrowseState =
    with(state) {
        when (event) {
            is BrowseEvent.BeginReplacement -> {
                copy(
                    criteria = event.criteria,
                    generation = event.generation,
                    items = if (event.keepItems) items else emptyList(),
                    nextPage = null,
                    activeReplacement = event.requestId,
                    append = AppendState.Idle,
                    load =
                        when {
                            event.debounce -> BrowseLoadState.Debouncing
                            event.keepItems && items.isNotEmpty() -> BrowseLoadState.Refreshing
                            else -> BrowseLoadState.Loading
                        },
                )
            }

            is BrowseEvent.Executing -> {
                if (matches(event.generation, event.requestId)) {
                    copy(
                        load =
                            if (items.isEmpty()) {
                                BrowseLoadState.Loading
                            } else {
                                BrowseLoadState.Refreshing
                            },
                    )
                } else {
                    this
                }
            }

            is BrowseEvent.Replaced -> {
                if (matches(event.generation, event.requestId)) {
                    if (event.nextPage != null && event.nextPage <= 0) {
                        copy(
                            load = BrowseLoadState.Failed(BrowseError.InvalidPage),
                            activeReplacement = null,
                        )
                    } else {
                        copy(
                            items = mergeCards(emptyList(), event.items),
                            nextPage = event.nextPage,
                            load = BrowseLoadState.Ready,
                            activeReplacement = null,
                        )
                    }
                } else {
                    this
                }
            }

            is BrowseEvent.ReplacementFailed -> {
                if (matches(event.generation, event.requestId)) {
                    copy(load = BrowseLoadState.Failed(event.error), activeReplacement = null)
                } else {
                    this
                }
            }

            is BrowseEvent.BeginAppend -> {
                copy(append = AppendState.Loading(event.requestId, event.page))
            }

            is BrowseEvent.Appended -> {
                if (matchesAppend(event.generation, event.requestId, event.page)) {
                    if (event.nextPage != null && event.nextPage <= event.page) {
                        copy(append = AppendState.Failed(BrowseError.InvalidPage))
                    } else {
                        copy(
                            items = mergeCards(items, event.items),
                            nextPage = event.nextPage,
                            append = AppendState.Idle,
                        )
                    }
                } else {
                    this
                }
            }

            is BrowseEvent.AppendFailed -> {
                if (matchesAppend(event.generation, event.requestId, event.page)) {
                    copy(append = AppendState.Failed(event.error))
                } else {
                    this
                }
            }

            BrowseEvent.LoadingLevels -> {
                copy(levels = LevelOptionsState.Loading)
            }

            is BrowseEvent.LevelsLoaded -> {
                copy(levels = LevelOptionsState.Ready(event.values.distinct()))
            }

            is BrowseEvent.LevelsFailed -> {
                copy(levels = LevelOptionsState.Failed(event.error))
            }
        }
    }

private fun BrowseState.matches(
    generation: Long,
    requestId: Long,
) = this.generation == generation && activeReplacement == requestId

private fun BrowseState.matchesAppend(
    generation: Long,
    requestId: Long,
    page: Int,
): Boolean {
    val loading = append as? AppendState.Loading ?: return false
    return this.generation == generation &&
        loading.requestId == requestId &&
        loading.page == page &&
        nextPage == page
}

private fun mergeCards(
    existing: List<DigimonCard>,
    incoming: List<DigimonCard>,
): List<DigimonCard> =
    linkedMapOf<Int, DigimonCard>()
        .apply {
            existing.forEach { put(it.id, it) }
            incoming.forEach { put(it.id, it) }
        }.values
        .toList()
