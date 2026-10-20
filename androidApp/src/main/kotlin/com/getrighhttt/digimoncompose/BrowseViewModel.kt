package com.getrighhttt.digimoncompose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.getrighhttt.digimoncompose.app.BrowseSession

/** Android retains the shared store across configuration changes. */
class BrowseViewModel : ViewModel() {
    private val session = BrowseSession(viewModelScope)
    val store get() = session.store
    val profileStore get() = session.profileStore
    override fun onCleared() { session.close() }
}
