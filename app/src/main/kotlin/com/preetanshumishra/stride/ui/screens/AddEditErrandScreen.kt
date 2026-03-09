package com.preetanshumishra.stride.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.preetanshumishra.stride.data.models.Errand
import com.preetanshumishra.stride.data.models.Place
import com.preetanshumishra.stride.services.ErrandService
import com.preetanshumishra.stride.services.PlaceService
import com.preetanshumishra.stride.ui.theme.StrideTheme
import com.preetanshumishra.stride.viewmodel.AddEditErrandViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditErrandScreen(
    existingErrand: Errand?,
    errandService: ErrandService,
    placeService: PlaceService,
    onNavigateBack: () -> Unit) {
    val viewModel: AddEditErrandViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                AddEditErrandViewModel(errandService, existingErrand, placeService) as T
        })

    val title by viewModel.title.collectAsState()
    val category by viewModel.category.collectAsState()
    val priority by viewModel.priority.collectAsState()
    val deadline by viewModel.deadline.collectAsState()
    val linkedPlaceId by viewModel.linkedPlaceId.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isSaved by viewModel.isSaved.collectAsState()
    val isRecurring by viewModel.isRecurring.collectAsState()
    val recurringInterval by viewModel.recurringInterval.collectAsState()
    val recurringUnit by viewModel.recurringUnit.collectAsState()
    val places by viewModel.places.collectAsState()

    LaunchedEffect(isSaved) {
        if (isSaved) onNavigateBack()
    }

    AddEditErrandContent(
        isEditing = viewModel.isEditing,
        title = title,
        onTitleChange = { viewModel.title.value = it },
        category = category,
        onCategoryChange = { viewModel.category.value = it },
        priority = priority,
        onPriorityChange = { viewModel.priority.value = it },
        deadline = deadline,
        onDeadlineChange = { viewModel.deadline.value = it },
        linkedPlaceId = linkedPlaceId,
        onLinkedPlaceIdChange = { viewModel.linkedPlaceId.value = it },
        isLoading = isLoading,
        errorMessage = errorMessage,
        isRecurring = isRecurring,
        onIsRecurringChange = { viewModel.isRecurring.value = it },
        recurringInterval = recurringInterval,
        onRecurringIntervalChange = { viewModel.recurringInterval.value = it },
        recurringUnit = recurringUnit,
        onRecurringUnitChange = { viewModel.recurringUnit.value = it },
        places = places,
        onSave = { viewModel.save() },
        onNavigateBack = onNavigateBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditErrandContent(
    isEditing: Boolean,
    title: String,
    onTitleChange: (String) -> Unit,
    category: String,
    onCategoryChange: (String) -> Unit,
    priority: String,
    onPriorityChange: (String) -> Unit,
    deadline: String,
    onDeadlineChange: (String) -> Unit,
    linkedPlaceId: String,
    onLinkedPlaceIdChange: (String) -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    isRecurring: Boolean,
    onIsRecurringChange: (Boolean) -> Unit,
    recurringInterval: Int,
    onRecurringIntervalChange: (Int) -> Unit,
    recurringUnit: String,
    onRecurringUnitChange: (String) -> Unit,
    places: List<Place>,
    onSave: () -> Unit,
    onNavigateBack: () -> Unit) {
    val priorities = listOf("low", "medium", "high")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Errand" else "Add Errand") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer))
        }) { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Title *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true)

            OutlinedTextField(
                value = category,
                onValueChange = onCategoryChange,
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true)

            Text("Priority", style = MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                priorities.forEach { p ->
                    FilterChip(
                        selected = priority == p,
                        onClick = { onPriorityChange(p) },
                        label = { Text(p.replaceFirstChar { it.uppercase() }) })
                }
            }

            OutlinedTextField(
                value = deadline,
                onValueChange = onDeadlineChange,
                label = { Text("Deadline (ISO 8601, optional)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. 2025-12-31T00:00:00Z") },
                singleLine = true)

            if (places.isEmpty()) {
                OutlinedTextField(
                    value = linkedPlaceId,
                    onValueChange = onLinkedPlaceIdChange,
                    label = { Text("Linked Place ID (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true)
            } else {
                var expanded by remember { mutableStateOf(false) }
                val selectedName = places.firstOrNull { it.id == linkedPlaceId }?.name ?: "None"
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = selectedName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Linked Place (optional)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor())
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(text = { Text("None") }, onClick = { onLinkedPlaceIdChange(""); expanded = false })
                        places.forEach { place ->
                            DropdownMenuItem(
                                text = { Text(place.name) },
                                onClick = { onLinkedPlaceIdChange(place.id); expanded = false })
                        }
                    }
                }
            }

            HorizontalDivider()
            Text("Recurring", style = MaterialTheme.typography.labelMedium)
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Repeat this errand", style = MaterialTheme.typography.bodyMedium)
                Switch(
                    checked = isRecurring,
                    onCheckedChange = onIsRecurringChange)
            }
            if (isRecurring) {
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = recurringInterval.toString(),
                        onValueChange = { onRecurringIntervalChange(it.toIntOrNull() ?: recurringInterval) },
                        label = { Text("Interval") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Unit", style = MaterialTheme.typography.labelSmall)
                        listOf("days", "weeks", "months").forEach { unit ->
                            FilterChip(
                                selected = recurringUnit == unit,
                                onClick = { onRecurringUnitChange(unit) },
                                label = { Text(unit.replaceFirstChar { it.uppercase() }) },
                                modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                }
            }

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Save")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddEditErrandPreview() {
    StrideTheme {
        AddEditErrandContent(
            isEditing = false,
            title = "Buy groceries",
            onTitleChange = {},
            category = "Shopping",
            onCategoryChange = {},
            priority = "medium",
            onPriorityChange = {},
            deadline = "2025-12-31T23:59:59Z",
            onDeadlineChange = {},
            linkedPlaceId = "1",
            onLinkedPlaceIdChange = {},
            isLoading = false,
            errorMessage = null,
            isRecurring = true,
            onIsRecurringChange = {},
            recurringInterval = 7,
            onRecurringIntervalChange = {},
            recurringUnit = "days",
            onRecurringUnitChange = {},
            places = listOf(
                Place("1", "Supermarket", "123 Main St", 0.0, 0.0),
                Place("2", "Pharmacy", "456 Oak Ave", 0.0, 0.0)),
            onSave = {},
            onNavigateBack = {})
    }
}