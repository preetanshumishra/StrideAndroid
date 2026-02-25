package com.preetanshumishra.stride.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.preetanshumishra.stride.services.AuthService
import com.preetanshumishra.stride.ui.screens.HomeScreen
import com.preetanshumishra.stride.ui.screens.LoginScreen
import com.preetanshumishra.stride.ui.screens.RegisterScreen
import com.preetanshumishra.stride.ui.screens.PlacesScreen
import com.preetanshumishra.stride.ui.screens.ErrandsScreen

@Composable
fun SetupNavGraph(authService: AuthService) {
    val isLoggedIn by authService.isLoggedIn.collectAsState()
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "home" else "login"
    ) {
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("register") {
            RegisterScreen(navController = navController)
        }
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("places") {
            PlacesScreen()
        }
        composable("errands") {
            ErrandsScreen()
        }
    }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate("home") {
                popUpTo(0) { inclusive = true }
            }
        } else {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }
}
