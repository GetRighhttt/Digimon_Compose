package com.getrighhttt.digimoncompose.presentation.browse.model

sealed interface AppendState {
    data object Idle : AppendState
    data class Loading(val requestId: Long, val page: Int) : AppendState
    data class Failed(val error: BrowseError) : AppendState
}
