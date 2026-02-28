package com.preetanshumishra.stride

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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

    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* permission result handled silently */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authService = appDependencies.authService

        lifecycleScope.launch {
            authService.checkPersistedAuth()
        }

        requestNotificationPermissionIfNeeded()

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

    override fun onResume() {
        super.onResume()
        appDependencies.woosmapManager.onResume()
    }

    override fun onPause() {
        super.onPause()
        appDependencies.woosmapManager.onPause()
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
