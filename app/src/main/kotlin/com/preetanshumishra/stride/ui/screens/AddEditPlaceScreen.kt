package com.preetanshumishra.stride.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.preetanshumishra.stride.data.models.Place
import com.preetanshumishra.stride.data.models.PlaceCollection
import com.preetanshumishra.stride.services.CollectionService
import com.preetanshumishra.stride.services.PlaceService
import com.preetanshumishra.stride.ui.theme.StrideTheme
import com.preetanshumishra.stride.viewmodel.AddEditPlaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPlaceScreen(
    existingPlace: Place?,
    placeService: PlaceService,
    collectionService: CollectionService,
    onNavigateBack: () -> Unit
) {
    val viewModel: AddEditPlaceViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                AddEditPlaceViewModel(placeService, existingPlace, collectionService) as T
        }
    )

    val name by viewModel.name.collectAsState()
    val address by viewModel.address.collectAsState()
    val latitudeText by viewModel.latitudeText.collectAsState()
    val longitudeText by viewModel.longitudeText.collectAsState()
    val category by viewModel.category.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val personalRating by viewModel.personalRating.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isSaved by viewModel.isSaved.collectAsState()
    val tagsText by viewModel.tagsText.collectAsState()
    val collectionId by viewModel.collectionId.collectAsState()
    val collections by viewModel.collections.collectAsState()

    LaunchedEffect(isSaved) {
        if (isSaved) onNavigateBack()
    }

    AddEditPlaceContent(
        isEditing = viewModel.isEditing,
        name = name,
        onNameChange = { viewModel.name.value = it },
        address = address,
        onAddressChange = { viewModel.address.value = it },
        latitudeText = latitudeText,
        onLatitudeTextChange = { viewModel.latitudeText.value = it },
        longitudeText = longitudeText,
        onLongitudeTextChange = { viewModel.longitudeText.value = it },
        category = category,
        onCategoryChange = { viewModel.category.value = it },
        notes = notes,
        onNotesChange = { viewModel.notes.value = it },
        personalRating = personalRating,
        onRatingChange = { viewModel.personalRating.value = it },
        tagsText = tagsText,
        onTagsTextChange = { viewModel.tagsText.value = it },
        collectionId = collectionId,
        onCollectionIdChange = { viewModel.collectionId.value = it },
        collections = collections,
        isLoading = isLoading,
        errorMessage = errorMessage,
        onSaveClick = { viewModel.save() },
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditPlaceContent(
    isEditing: Boolean,
    name: String,
    onNameChange: (String) -> Unit,
    address: String,
    onAddressChange: (String) -> Unit,
    latitudeText: String,
    onLatitudeTextChange: (String) -> Unit,
    longitudeText: String,
    onLongitudeTextChange: (String) -> Unit,
    category: String,
    onCategoryChange: (String) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    personalRating: Int,
    onRatingChange: (Int) -> Unit,
    tagsText: String,
    onTagsTextChange: (String) -> Unit,
    collectionId: String,
    onCollectionIdChange: (String) -> Unit,
    collections: List<PlaceCollection>,
    isLoading: Boolean,
    errorMessage: String?,
    onSaveClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Place" else "Add Place") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Name *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = address,
                onValueChange = onAddressChange,
                label = { Text("Address *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = latitudeText,
                    onValueChange = onLatitudeTextChange,
                    label = { Text("Latitude *") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                OutlinedTextField(
                    value = longitudeText,
                    onValueChange = onLongitudeTextChange,
                    label = { Text("Longitude *") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = category,
                onValueChange = onCategoryChange,
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = notes,
                onValueChange = onNotesChange,
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            Text("Rating: ${if (personalRating == 0) "None" else "$personalRating / 5"}")
            Slider(
                value = personalRating.toFloat(),
                onValueChange = { onRatingChange(it.toInt()) },
                valueRange = 0f..5f,
                steps = 4,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = tagsText,
                onValueChange = onTagsTextChange,
                label = { Text("Tags (comma-separated)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. coffee, grocery, pharmacy") },
                singleLine = true
            )

            if (collections.isNotEmpty()) {
                var expanded by remember { mutableStateOf(false) }
                val selectedName = collections.firstOrNull { it.id == collectionId }?.name ?: "None"
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = selectedName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Collection") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(text = { Text("None") }, onClick = { onCollectionIdChange(""); expanded = false })
                        collections.forEach { collection ->
                            DropdownMenuItem(
                                text = { Text(collection.name) },
                                onClick = { onCollectionIdChange(collection.id); expanded = false }
                            )
                        }
                    }
                }
            }

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddEditPlaceScreenPreview() {
    StrideTheme {
        AddEditPlaceContent(
            isEditing = false,
            name = "Central Park",
            onNameChange = {},
            address = "New York, NY",
            onAddressChange = {},
            latitudeText = "40.785091",
            onLatitudeTextChange = {},
            longitudeText = "-73.968285",
            onLongitudeTextChange = {},
            category = "Park",
            onCategoryChange = {},
            notes = "Beautiful place for a walk",
            onNotesChange = {},
            personalRating = 5,
            onRatingChange = {},
            tagsText = "nature, outdoors, recreation",
            onTagsTextChange = {},
            collectionId = "1",
            onCollectionIdChange = {},
            collections = listOf(
                PlaceCollection("1", "Favorites", "⭐"),
                PlaceCollection("2", "Work", "💼")
            ),
            isLoading = false,
            errorMessage = null,
            onSaveClick = {},
            onNavigateBack = {}
        )
    }
}
