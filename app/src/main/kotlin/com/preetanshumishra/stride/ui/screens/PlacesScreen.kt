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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavController
import com.google.gson.Gson
import com.preetanshumishra.stride.data.models.Place
import com.preetanshumishra.stride.data.models.PlaceCollection
import com.preetanshumishra.stride.ui.components.PlaceCard
import com.preetanshumishra.stride.ui.theme.StrideTheme
import com.preetanshumishra.stride.viewmodel.PlacesViewModel
import com.preetanshumishra.stride.viewmodel.ViewModelFactory
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun PlacesScreen(navController: NavController) {
    if (LocalInspectionMode.current) {
        PlacesContent(
            isLoading = false,
            errorMessage = null,
            searchQuery = "",
            filteredPlaces = emptyList(),
            collections = emptyList(),
            collectionFilter = null,
            onRefresh = {},
            onAddPlace = {},
            onSearchQueryChange = {},
            onCollectionFilterChange = {},
            onEditPlace = {},
            onDeletePlace = {},
            onRecordVisit = {}
        )
        return
    }

    val owner = LocalViewModelStoreOwner.current ?: error("No ViewModel store owner found")
    val viewModel = remember(owner) {
        ViewModelProvider(owner.viewModelStore, ViewModelFactory())[PlacesViewModel::class.java]
    }
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

    PlacesContent(
        isLoading = isLoading,
        errorMessage = errorMessage,
        searchQuery = searchQuery,
        filteredPlaces = filteredPlaces,
        collections = collections,
        collectionFilter = collectionFilter,
        onRefresh = { viewModel.loadPlaces() },
        onAddPlace = { navController.navigate("addPlace") },
        onSearchQueryChange = { viewModel.setSearchQuery(it) },
        onCollectionFilterChange = { viewModel.setCollectionFilter(it) },
        onEditPlace = { place ->
            val json = URLEncoder.encode(Gson().toJson(place), StandardCharsets.UTF_8.toString())
            navController.navigate("editPlace/$json")
        },
        onDeletePlace = { viewModel.deletePlace(it) },
        onRecordVisit = { viewModel.recordVisit(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacesContent(
    isLoading: Boolean,
    errorMessage: String?,
    searchQuery: String,
    filteredPlaces: List<Place>,
    collections: List<PlaceCollection>,
    collectionFilter: String?,
    onRefresh: () -> Unit,
    onAddPlace: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onCollectionFilterChange: (String?) -> Unit,
    onEditPlace: (Place) -> Unit,
    onDeletePlace: (String) -> Unit,
    onRecordVisit: (String) -> Unit
) {
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
                onClick = onAddPlace,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Place")
            }
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = onRefresh,
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
                            onClick = { onCollectionFilterChange(null) },
                            label = { Text("All") }
                        )
                        collections.forEach { collection ->
                            FilterChip(
                                selected = collectionFilter == collection.id,
                                onClick = { onCollectionFilterChange(collection.id) },
                                label = { Text(collection.name) }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
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
                                onEdit = { onEditPlace(place) },
                                onDelete = { onDeletePlace(place.id) },
                                onRecordVisit = { onRecordVisit(place.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlacesScreenPreview() {
    StrideTheme {
        PlacesContent(
            isLoading = false,
            errorMessage = null,
            searchQuery = "",
            filteredPlaces = listOf(
                Place(
                    id = "1",
                    name = "Central Park",
                    address = "New York, NY",
                    latitude = 40.785091,
                    longitude = -73.968285,
                    category = "Park",
                    tags = listOf("nature", "outdoor"),
                    visitCount = 3
                ),
                Place(
                    id = "2",
                    name = "Joe's Coffee",
                    address = "123 Main St",
                    latitude = 40.712776,
                    longitude = -74.005974,
                    category = "Cafe",
                    tags = listOf("coffee", "wifi"),
                    visitCount = 1
                )
            ),
            collections = listOf(
                PlaceCollection(id = "c1", name = "Favorites"),
                PlaceCollection(id = "c2", name = "Work")
            ),
            collectionFilter = null,
            onRefresh = {},
            onAddPlace = {},
            onSearchQueryChange = {},
            onCollectionFilterChange = {},
            onEditPlace = {},
            onDeletePlace = {},
            onRecordVisit = {}
        )
    }
}
