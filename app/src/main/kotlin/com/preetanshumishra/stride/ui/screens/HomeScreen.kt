package com.preetanshumishra.stride.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavController
import com.preetanshumishra.stride.data.models.User
import com.preetanshumishra.stride.ui.components.NavigationCard
import com.preetanshumishra.stride.ui.theme.StrideTheme
import com.preetanshumishra.stride.viewmodel.HomeViewModel
import com.preetanshumishra.stride.viewmodel.ViewModelFactory

@Composable
fun HomeScreen(
    navController: NavController? = null
) {
    // Check if we are in Preview mode to avoid ViewModel initialization that depends on appDependencies
    if (LocalInspectionMode.current) {
        HomeScreenContent(
            user = User(
                id = "1",
                email = "john.doe@example.com",
                firstName = "John",
                lastName = "Doe"
            ),
            onLogout = {},
            onNavigate = { route -> navController?.navigate(route) }
        )
        return
    }

    val owner = LocalViewModelStoreOwner.current ?: error("No ViewModel store owner found")
    val viewModel = remember(owner) {
        ViewModelProvider(owner.viewModelStore, ViewModelFactory())[HomeViewModel::class.java]
    }
    val user by viewModel.user.collectAsState()

    HomeScreenContent(
        user = user,
        onLogout = { viewModel.logout() },
        onNavigate = { route -> navController?.navigate(route) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    user: User?,
    onLogout: () -> Unit,
    onNavigate: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") },
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
        ) {
            user?.let { currentUser ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Welcome, ${currentUser.firstName}!",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = currentUser.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            NavigationCard(
                icon = Icons.Default.Place,
                title = "Places",
                onClick = { onNavigate("places") }
            )

            NavigationCard(
                icon = Icons.Default.DateRange,
                title = "Errands",
                onClick = { onNavigate("errands") }
            )

            NavigationCard(
                icon = Icons.Default.Folder,
                title = "Collections",
                onClick = { onNavigate("collections") }
            )

            NavigationCard(
                icon = Icons.Default.Navigation,
                title = "Smart Route",
                onClick = { onNavigate("smartRoute") }
            )

            NavigationCard(
                icon = Icons.Default.LocationOn,
                title = "Nearby",
                onClick = { onNavigate("nearby") }
            )

            NavigationCard(
                icon = Icons.Default.Settings,
                title = "Settings",
                onClick = { onNavigate("settings") }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Logout")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    StrideTheme {
        HomeScreenContent(
            user = User(
                id = "1",
                email = "john.doe@example.com",
                firstName = "John",
                lastName = "Doe"
            ),
            onLogout = {},
            onNavigate = {}
        )
    }
}
