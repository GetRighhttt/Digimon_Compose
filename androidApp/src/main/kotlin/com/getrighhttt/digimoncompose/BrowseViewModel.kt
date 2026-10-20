package com.getrighhttt.digimoncompose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.getrighhttt.digimoncompose.app.BrowseSession

/**
 * Owns both shared feature stores and their HTTP session across Android configuration
 * changes.
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
