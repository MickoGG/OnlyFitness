package com.nemanjamicanovic.only_fitness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nemanjamicanovic.only_fitness.ui.screens.AuthNavigation
import com.nemanjamicanovic.only_fitness.ui.theme.OnlyFitnessTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OnlyFitnessTheme {
                AuthNavigation()
            }
        }
    }
}
