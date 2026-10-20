package com.getrighhttt.digimoncompose.app

import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.getrighhttt.digimoncompose.presentation.browse.BrowseScreen
import com.getrighhttt.digimoncompose.presentation.browse.BrowseStore
import com.getrighhttt.digimoncompose.presentation.browse.model.BrowseIntent
import com.getrighhttt.digimoncompose.presentation.design.FieldGuideTheme
import com.getrighhttt.digimoncompose.presentation.design.DigimonSplash
import com.getrighhttt.digimoncompose.presentation.profile.ProfileScreen
import com.getrighhttt.digimoncompose.presentation.profile.ProfileStore
import com.getrighhttt.digimoncompose.presentation.profile.model.*

/**
 * Coordinates splash and profile selection while Browse keeps its owner and scroll state.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Composable
fun DigimonApp(store: BrowseStore, profileStore: ProfileStore) = FieldGuideTheme {
    val state by store.state.collectAsStateWithLifecycle()
    val profileState by profileStore.state.collectAsStateWithLifecycle()
    val gridState = rememberLazyGridState()
    var showSplash by rememberSaveable { mutableStateOf(true) }
    var selectedId by rememberSaveable { mutableStateOf<Int?>(null) }
    var selectedName by rememberSaveable { mutableStateOf("") }
    var selectedImage by rememberSaveable { mutableStateOf<String?>(null) }
    LaunchedEffect(store) { store.dispatch(BrowseIntent.Started) }
    LaunchedEffect(state.criteria) { gridState.scrollToItem(0) }
    LaunchedEffect(selectedId, profileStore) {
        selectedId?.let { profileStore.dispatch(ProfileIntent.Open(it)) }
            ?: profileStore.dispatch(ProfileIntent.Closed)
    }
    PlatformBackHandler(selectedId != null) { selectedId = null }
    if (showSplash) {
        DigimonSplash { showSplash = false }
    } else if (selectedId == null) {
        BrowseScreen(state, gridState, store::dispatch) {
            selectedId = it.id; selectedName = it.name; selectedImage = it.image
        }
    } else {
        val displayed = if (profileState.id == selectedId) profileState else ProfileState(id = selectedId, loading = true)
        ProfileScreen(displayed, selectedName, selectedImage, { selectedId = null }, { profileStore.dispatch(ProfileIntent.Retry) })
    }
}
