package com.preetanshumishra.stride.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavController
import com.google.gson.Gson
import com.preetanshumishra.stride.ui.components.PlaceCard
import com.preetanshumishra.stride.viewmodel.PlacesViewModel
import com.preetanshumishra.stride.viewmodel.ViewModelFactory
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacesScreen(navController: NavController) {
    val owner = LocalViewModelStoreOwner.current ?: error("No ViewModel store owner found")
    val viewModel = remember(owner) {
        ViewModelProvider(owner.viewModelStore, ViewModelFactory()).get(PlacesViewModel::class.java)
    }
    val places by viewModel.places.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredPlaces by viewModel.filteredPlaces.collectAsState()
    val collections by viewModel.collections.collectAsState()
    val collectionFilter by viewModel.collectionFilter.collectAsState()

    val currentBackStackEntry = navController.currentBackStackEntry
    LaunchedEffect(currentBackStackEntry) {
        val refresh = currentBackStackEntry?.savedStateHandle?.remove<Boolean>("refreshPlaces")
        if (refresh == true) viewModel.loadPlaces()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Places") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addPlace") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Place")
            }
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { viewModel.loadPlaces() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                errorMessage?.let { error ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                if (collections.isNotEmpty()) {
                    val scrollState = rememberScrollState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState)
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = collectionFilter == null,
                            onClick = { viewModel.setCollectionFilter(null) },
                            label = { Text("All") }
                        )
                        collections.forEach { collection ->
                            FilterChip(
                                selected = collectionFilter == collection.id,
                                onClick = { viewModel.setCollectionFilter(collection.id) },
                                label = { Text(collection.name) }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    label = { Text("Search places") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    singleLine = true
                )

                if (filteredPlaces.isEmpty() && !isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No places saved yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn {
                        items(filteredPlaces) { place ->
                            PlaceCard(
                                place = place,
                                onEdit = {
                                    val json = URLEncoder.encode(Gson().toJson(place), StandardCharsets.UTF_8.toString())
                                    navController.navigate("editPlace/$json")
                                },
                                onDelete = { viewModel.deletePlace(place.id) },
                                onRecordVisit = { viewModel.recordVisit(place.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
