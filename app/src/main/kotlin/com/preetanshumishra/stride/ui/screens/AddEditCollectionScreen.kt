package com.preetanshumishra.stride.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.preetanshumishra.stride.data.models.PlaceCollection
import com.preetanshumishra.stride.services.CollectionService
import com.preetanshumishra.stride.viewmodel.AddEditCollectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCollectionScreen(
    existingCollection: PlaceCollection?,
    collectionService: CollectionService,
    onNavigateBack: () -> Unit
) {
    val owner = LocalViewModelStoreOwner.current ?: error("No ViewModel store owner found")
    val viewModel = remember(owner) {
        ViewModelProvider(owner.viewModelStore, object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AddEditCollectionViewModel(
                    collectionService,
                    existingCollection
                ) as T
            }
        }).get(AddEditCollectionViewModel::class.java)
    }

    val name by viewModel.name.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val didSave by viewModel.didSave.collectAsState()

    LaunchedEffect(didSave) {
        if (didSave) onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.isEditing) "Edit Collection" else "New Collection") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            errorMessage?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            OutlinedTextField(
                value = name,
                onValueChange = { viewModel.name.value = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = { viewModel.save() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                else Text(if (viewModel.isEditing) "Save Changes" else "Add Collection")
            }
        }
    }
}
