package com.getrighhttt.digimoncompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.getrighhttt.digimoncompose.app.DigimonApp

/** Android owns store retention; shared Compose owns safe drawing insets. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val owner = ViewModelProvider(this)[BrowseViewModel::class.java]
        setContent { DigimonApp(owner.store, owner.profileStore) }
    }
}
