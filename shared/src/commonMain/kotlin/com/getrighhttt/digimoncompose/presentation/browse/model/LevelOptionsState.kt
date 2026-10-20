package com.getrighhttt.digimoncompose.presentation.browse.model

sealed interface LevelOptionsState {
    data object Loading : LevelOptionsState
    data class Ready(val values: List<String>) : LevelOptionsState
    data class Failed(val error: BrowseError) : LevelOptionsState
}
