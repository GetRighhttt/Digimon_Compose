package com.getrighhttt.digimoncompose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.getrighhttt.digimoncompose.app.BrowseSession

/**
 * Keeps the catalog and profile stores alive when Android recreates the activity.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
class BrowseViewModel : ViewModel() {
    private val session = BrowseSession(viewModelScope)
    val store
        get() = session.store

    val profileStore
        get() = session.profileStore

    override fun onCleared() {
        session.close()
    }
}
