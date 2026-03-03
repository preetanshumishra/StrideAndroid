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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavController
import com.google.gson.Gson
import com.preetanshumishra.stride.data.models.Errand
import com.preetanshumishra.stride.ui.components.ErrandCard
import com.preetanshumishra.stride.ui.theme.StrideTheme
import com.preetanshumishra.stride.viewmodel.ErrandsViewModel
import com.preetanshumishra.stride.viewmodel.ViewModelFactory
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun ErrandsScreen(navController: NavController) {
    if (LocalInspectionMode.current) {
        ErrandsScreenContent(
            isLoading = false,
            errorMessage = null,
            statusFilter = "all",
            priorityFilter = "all",
            filteredErrands = emptyList(),
            onRefresh = {},
            onStatusFilterChange = {},
            onPriorityFilterChange = {},
            onAddErrand = {},
            onEditErrand = {},
            onCompleteErrand = {},
            onDeleteErrand = {}
        )
        return
    }

    val owner = LocalViewModelStoreOwner.current ?: error("No ViewModel store owner found")
    val viewModel = remember(owner) {
        ViewModelProvider(owner.viewModelStore, ViewModelFactory())[ErrandsViewModel::class.java]
    }
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()
    val priorityFilter by viewModel.priorityFilter.collectAsState()
    val filteredErrands by viewModel.filteredErrands.collectAsState()

    val currentBackStackEntry = navController.currentBackStackEntry
    LaunchedEffect(currentBackStackEntry) {
        val refresh = currentBackStackEntry?.savedStateHandle?.remove<Boolean>("refreshErrands")
        if (refresh == true) viewModel.loadErrands()
    }

    ErrandsScreenContent(
        isLoading = isLoading,
        errorMessage = errorMessage,
        statusFilter = statusFilter,
        priorityFilter = priorityFilter,
        filteredErrands = filteredErrands,
        onRefresh = { viewModel.loadErrands() },
        onStatusFilterChange = { viewModel.setStatusFilter(it) },
        onPriorityFilterChange = { viewModel.setPriorityFilter(it) },
        onAddErrand = { navController.navigate("addErrand") },
        onEditErrand = { errand ->
            val json = URLEncoder.encode(Gson().toJson(errand), StandardCharsets.UTF_8.toString())
            navController.navigate("editErrand/$json")
        },
        onCompleteErrand = { viewModel.completeErrand(it) },
        onDeleteErrand = { viewModel.deleteErrand(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrandsScreenContent(
    isLoading: Boolean,
    errorMessage: String?,
    statusFilter: String,
    priorityFilter: String,
    filteredErrands: List<Errand>,
    onRefresh: () -> Unit,
    onStatusFilterChange: (String) -> Unit,
    onPriorityFilterChange: (String) -> Unit,
    onAddErrand: () -> Unit,
    onEditErrand: (Errand) -> Unit,
    onCompleteErrand: (String) -> Unit,
    onDeleteErrand: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Errands") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddErrand,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Errand")
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

                // Status filter chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("all" to "All", "pending" to "Pending", "done" to "Done").forEach { (value, label) ->
                        FilterChip(
                            selected = statusFilter == value,
                            onClick = { onStatusFilterChange(value) },
                            label = { Text(label) }
                        )
                    }
                }
                // Priority filter chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("all" to "All", "low" to "Low", "medium" to "Medium", "high" to "High").forEach { (value, label) ->
                        FilterChip(
                            selected = priorityFilter == value,
                            onClick = { onPriorityFilterChange(value) },
                            label = { Text(label) }
                        )
                    }
                }

                if (filteredErrands.isEmpty() && !isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No errands yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn {
                        items(filteredErrands) { errand ->
                            ErrandCard(
                                errand = errand,
                                onEdit = { onEditErrand(errand) },
                                onComplete = { onCompleteErrand(errand.id) },
                                onDelete = { onDeleteErrand(errand.id) }
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
fun ErrandsScreenPreview() {
    StrideTheme {
        ErrandsScreenContent(
            isLoading = false,
            errorMessage = null,
            statusFilter = "all",
            priorityFilter = "all",
            filteredErrands = listOf(
                Errand(id = "1", title = "Buy groceries", category = "Shopping", priority = "high", status = "pending"),
                Errand(id = "2", title = "Walk the dog", category = "Pet", priority = "medium", status = "pending"),
                Errand(id = "3", title = "Clean the house", category = "Chore", priority = "low", status = "done")
            ),
            onRefresh = {},
            onStatusFilterChange = {},
            onPriorityFilterChange = {},
            onAddErrand = {},
            onEditErrand = {},
            onCompleteErrand = {},
            onDeleteErrand = {}
        )
    }
}
