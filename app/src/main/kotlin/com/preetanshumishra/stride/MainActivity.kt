package com.preetanshumishra.stride

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.preetanshumishra.stride.di.appDependencies
import com.preetanshumishra.stride.ui.navigation.SetupNavGraph
import com.preetanshumishra.stride.ui.theme.StrideTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authService = appDependencies.authService

        lifecycleScope.launch {
            authService.checkPersistedAuth()
        }

        setContent {
            StrideTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SetupNavGraph(authService = authService)
                }
            }
        }
    }
}
