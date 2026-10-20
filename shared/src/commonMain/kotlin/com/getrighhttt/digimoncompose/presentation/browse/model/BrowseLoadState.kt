package com.getrighhttt.digimoncompose.presentation.browse.model

sealed interface BrowseLoadState {
    data object Idle : BrowseLoadState
    data object Debouncing : BrowseLoadState
    data object Loading : BrowseLoadState
    data object Refreshing : BrowseLoadState
    data object Ready : BrowseLoadState
    data class Failed(val error: BrowseError) : BrowseLoadState
}
