package com.getrighhttt.digimoncompose.presentation.browse.model

/**
 * Tracks stage-option loading and retry independently of catalog content.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
sealed interface LevelOptionsState {
    data object Loading : LevelOptionsState
    data class Ready(val values: List<String>) : LevelOptionsState
    data class Failed(val error: BrowseError) : LevelOptionsState
}
