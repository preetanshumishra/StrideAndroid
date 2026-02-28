package com.preetanshumishra.stride.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavController
import com.google.gson.Gson
import com.preetanshumishra.stride.data.models.PlaceCollection
import com.preetanshumishra.stride.viewmodel.CollectionsViewModel
import com.preetanshumishra.stride.viewmodel.ViewModelFactory
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsScreen(navController: NavController) {
    val owner = LocalViewModelStoreOwner.current ?: error("No ViewModel store owner found")
    val viewModel = remember(owner) {
        ViewModelProvider(owner.viewModelStore, ViewModelFactory()).get(CollectionsViewModel::class.java)
    }
    val collections by viewModel.collections.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val currentBackStackEntry = navController.currentBackStackEntry
    LaunchedEffect(currentBackStackEntry) {
        val refresh = currentBackStackEntry?.savedStateHandle?.remove<Boolean>("refreshCollections")
        if (refresh == true) viewModel.loadCollections()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Collections") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addCollection") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Collection")
            }
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { viewModel.loadCollections() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                errorMessage != null -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadCollections() }) { Text("Retry") }
                }
                collections.isEmpty() && !isLoading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No collections yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(collections, key = { it.id }) { collection ->
                        CollectionCard(
                            collection = collection,
                            onEdit = {
                                val json = URLEncoder.encode(
                                    Gson().toJson(collection),
                                    StandardCharsets.UTF_8.toString()
                                )
                                navController.navigate("editCollection/$json")
                            },
                            onDelete = { viewModel.deleteCollection(collection.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CollectionCard(
    collection: PlaceCollection,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Collection") },
            text = { Text("Delete \"${collection.name}\"?") },
            confirmButton = {
                TextButton(onClick = { showDeleteConfirm = false; onDelete() }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    collection.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (collection.icon.isNotBlank()) {
                    Text(
                        collection.icon,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row {
                TextButton(onClick = onEdit) { Text("Edit") }
                TextButton(onClick = { showDeleteConfirm = true }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
