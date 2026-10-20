package com.getrighhttt.digimoncompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.getrighhttt.digimoncompose.app.DigimonApp

/**
 * Hosts the shared app with edge-to-edge drawing and an Android-retained session.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val owner = ViewModelProvider(this)[BrowseViewModel::class.java]
        setContent { DigimonApp(owner.store, owner.profileStore) }
    }
}
