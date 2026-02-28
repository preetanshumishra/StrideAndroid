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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavController
import com.preetanshumishra.stride.viewmodel.SettingsViewModel
import com.preetanshumishra.stride.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val owner = LocalViewModelStoreOwner.current ?: error("No ViewModel store owner found")
    val viewModel = remember(owner) {
        ViewModelProvider(owner.viewModelStore, ViewModelFactory()).get(SettingsViewModel::class.java)
    }

    val firstName by viewModel.firstName.collectAsState()
    val lastName by viewModel.lastName.collectAsState()
    val email by viewModel.email.collectAsState()
    val currentPassword by viewModel.currentPassword.collectAsState()
    val newPassword by viewModel.newPassword.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val showDeleteConfirm by viewModel.showDeleteConfirm.collectAsState()

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteConfirm() },
            title = { Text("Delete Account") },
            text = { Text("This will permanently delete your account and all data. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmDeleteAccount() }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissDeleteConfirm() }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
            successMessage?.let {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Text(it, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall)
                }
            }
            errorMessage?.let {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Text(it, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }

            Text("Profile", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(value = firstName, onValueChange = { viewModel.firstName.value = it },
                label = { Text("First Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = lastName, onValueChange = { viewModel.lastName.value = it },
                label = { Text("Last Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = email, onValueChange = { viewModel.email.value = it },
                label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
            Button(onClick = { viewModel.saveProfile() }, modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading) { Text("Save Profile") }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text("Change Password", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(value = currentPassword, onValueChange = { viewModel.currentPassword.value = it },
                label = { Text("Current Password") }, modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(), singleLine = true)
            OutlinedTextField(value = newPassword, onValueChange = { viewModel.newPassword.value = it },
                label = { Text("New Password") }, modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(), singleLine = true)
            OutlinedTextField(value = confirmPassword, onValueChange = { viewModel.confirmPassword.value = it },
                label = { Text("Confirm New Password") }, modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(), singleLine = true)
            Button(onClick = { viewModel.changePassword() }, modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading) { Text("Change Password") }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Button(
                onClick = { viewModel.requestDeleteAccount() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                enabled = !isLoading
            ) { Text("Delete Account") }
        }
    }
}
