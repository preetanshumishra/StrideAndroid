package com.preetanshumishra.stride.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.preetanshumishra.stride.data.models.Errand
import com.preetanshumishra.stride.ui.theme.StrideTheme
import com.preetanshumishra.stride.viewmodel.SmartRouteViewModel
import com.preetanshumishra.stride.viewmodel.ViewModelFactory

@Composable
fun SmartRouteScreen() {
    if (LocalInspectionMode.current) {
        SmartRouteScreenContent(
            routedErrands = emptyList(),
            isLoading = false,
            errorMessage = null,
            onRefresh = {}
        )
        return
    }

    val owner = LocalViewModelStoreOwner.current ?: error("No ViewModel store owner found")
    val viewModel = remember(owner) {
        ViewModelProvider(owner.viewModelStore, ViewModelFactory())[SmartRouteViewModel::class.java]
    }
    val routedErrands by viewModel.routedErrands.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val needsPermission by viewModel.needsPermission.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) viewModel.permissionGranted()
    }

    LaunchedEffect(needsPermission) {
        if (needsPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    SmartRouteScreenContent(
        routedErrands = routedErrands,
        isLoading = isLoading,
        errorMessage = errorMessage,
        onRefresh = { viewModel.fetchRoute() }
    )

    LaunchedEffect(Unit) { viewModel.fetchRoute() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartRouteScreenContent(
    routedErrands: List<Errand>,
    isLoading: Boolean,
    errorMessage: String?,
    onRefresh: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Smart Route") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                errorMessage != null -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRefresh) { Text("Try Again") }
                }
                routedErrands.isEmpty() -> Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No pending errands to route",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRefresh) { Text("Get Route") }
                }
                else -> LazyColumn(modifier = Modifier.padding(16.dp)) {
                    itemsIndexed(routedErrands) { index, errand ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(end = 12.dp)
                                    )
                                    Column {
                                        Text(
                                            text = errand.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = errand.priority.replaceFirstChar { it.uppercase() },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                errand.distanceKm?.let { dist ->
                                    SuggestionChip(
                                        onClick = {},
                                        label = { Text(String.format("%.1f km", dist)) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SmartRouteScreenPreview() {
    StrideTheme {
        SmartRouteScreenContent(
            routedErrands = listOf(
                Errand(id = "1", title = "Buy Groceries", priority = "high", distanceKm = 1.2),
                Errand(id = "2", title = "Pick up Laundry", priority = "medium", distanceKm = 2.5),
                Errand(id = "3", title = "Drop off Package", priority = "low", distanceKm = 3.8)
            ),
            isLoading = false,
            errorMessage = null,
            onRefresh = {}
        )
    }
}
