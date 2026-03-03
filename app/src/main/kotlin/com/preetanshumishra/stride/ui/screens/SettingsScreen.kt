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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavController
import com.preetanshumishra.stride.ui.theme.StrideTheme
import com.preetanshumishra.stride.viewmodel.SettingsViewModel
import com.preetanshumishra.stride.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    if (LocalInspectionMode.current) {
        SettingsScreenContent(
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            currentPassword = "",
            newPassword = "",
            confirmPassword = "",
            isLoading = false,
            successMessage = null,
            errorMessage = null,
            showDeleteConfirm = false,
            onFirstNameChange = {},
            onLastNameChange = {},
            onEmailChange = {},
            onCurrentPasswordChange = {},
            onNewPasswordChange = {},
            onConfirmPasswordChange = {},
            onSaveProfile = {},
            onChangePassword = {},
            onRequestDeleteAccount = {},
            onConfirmDeleteAccount = {},
            onDismissDeleteConfirm = {},
            onBack = {}
        )
        return
    }

    val owner = LocalViewModelStoreOwner.current ?: error("No ViewModel store owner found")
    val viewModel = remember(owner) {
        ViewModelProvider(owner.viewModelStore, ViewModelFactory())[SettingsViewModel::class.java]
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

    SettingsScreenContent(
        firstName = firstName,
        lastName = lastName,
        email = email,
        currentPassword = currentPassword,
        newPassword = newPassword,
        confirmPassword = confirmPassword,
        isLoading = isLoading,
        successMessage = successMessage,
        errorMessage = errorMessage,
        showDeleteConfirm = showDeleteConfirm,
        onFirstNameChange = { viewModel.firstName.value = it },
        onLastNameChange = { viewModel.lastName.value = it },
        onEmailChange = { viewModel.email.value = it },
        onCurrentPasswordChange = { viewModel.currentPassword.value = it },
        onNewPasswordChange = { viewModel.newPassword.value = it },
        onConfirmPasswordChange = { viewModel.confirmPassword.value = it },
        onSaveProfile = { viewModel.saveProfile() },
        onChangePassword = { viewModel.changePassword() },
        onRequestDeleteAccount = { viewModel.requestDeleteAccount() },
        onConfirmDeleteAccount = { viewModel.confirmDeleteAccount() },
        onDismissDeleteConfirm = { viewModel.dismissDeleteConfirm() },
        onBack = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    firstName: String,
    lastName: String,
    email: String,
    currentPassword: String,
    newPassword: String,
    confirmPassword: String,
    isLoading: Boolean,
    successMessage: String?,
    errorMessage: String?,
    showDeleteConfirm: Boolean,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onCurrentPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSaveProfile: () -> Unit,
    onChangePassword: () -> Unit,
    onRequestDeleteAccount: () -> Unit,
    onConfirmDeleteAccount: () -> Unit,
    onDismissDeleteConfirm: () -> Unit,
    onBack: () -> Unit
) {
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = onDismissDeleteConfirm,
            title = { Text("Delete Account") },
            text = { Text("This will permanently delete your account and all data. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = onConfirmDeleteAccount) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDeleteConfirm) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
            OutlinedTextField(value = firstName, onValueChange = onFirstNameChange,
                label = { Text("First Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = lastName, onValueChange = onLastNameChange,
                label = { Text("Last Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = email, onValueChange = onEmailChange,
                label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
            Button(onClick = onSaveProfile, modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading) { Text("Save Profile") }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text("Change Password", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(value = currentPassword, onValueChange = onCurrentPasswordChange,
                label = { Text("Current Password") }, modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(), singleLine = true)
            OutlinedTextField(value = newPassword, onValueChange = onNewPasswordChange,
                label = { Text("New Password") }, modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(), singleLine = true)
            OutlinedTextField(value = confirmPassword, onValueChange = onConfirmPasswordChange,
                label = { Text("Confirm New Password") }, modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(), singleLine = true)
            Button(onClick = onChangePassword, modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading) { Text("Change Password") }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Button(
                onClick = onRequestDeleteAccount,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                enabled = !isLoading
            ) { Text("Delete Account") }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    StrideTheme {
        SettingsScreenContent(
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            currentPassword = "",
            newPassword = "",
            confirmPassword = "",
            isLoading = false,
            successMessage = null,
            errorMessage = null,
            showDeleteConfirm = false,
            onFirstNameChange = {},
            onLastNameChange = {},
            onEmailChange = {},
            onCurrentPasswordChange = {},
            onNewPasswordChange = {},
            onConfirmPasswordChange = {},
            onSaveProfile = {},
            onChangePassword = {},
            onRequestDeleteAccount = {},
            onConfirmDeleteAccount = {},
            onDismissDeleteConfirm = {},
            onBack = {}
        )
    }
}
