package com.getrighhttt.digimoncompose.domain.browse.model

/** Failures are typed so presentation never depends on exception text. */
sealed interface BrowseResult<out T> {
    data class Success<T>(val value: T) : BrowseResult<T>
    data class Failure(val reason: BrowseFailure) : BrowseResult<Nothing>
}
