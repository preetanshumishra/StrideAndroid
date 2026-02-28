package com.preetanshumishra.stride.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.preetanshumishra.stride.data.models.Errand
import com.preetanshumishra.stride.data.models.Place
import com.preetanshumishra.stride.data.models.PlaceCollection
import com.preetanshumishra.stride.di.appDependencies
import com.preetanshumishra.stride.services.AuthService
import com.preetanshumishra.stride.ui.screens.AddEditCollectionScreen
import com.preetanshumishra.stride.ui.screens.AddEditErrandScreen
import com.preetanshumishra.stride.ui.screens.AddEditPlaceScreen
import com.preetanshumishra.stride.ui.screens.CollectionsScreen
import com.preetanshumishra.stride.ui.screens.ErrandsScreen
import com.preetanshumishra.stride.ui.screens.HomeScreen
import com.preetanshumishra.stride.ui.screens.LoginScreen
import com.preetanshumishra.stride.ui.screens.NearbyScreen
import com.preetanshumishra.stride.ui.screens.PlacesScreen
import com.preetanshumishra.stride.ui.screens.RegisterScreen
import com.preetanshumishra.stride.ui.screens.SettingsScreen
import com.preetanshumishra.stride.ui.screens.SmartRouteScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun SetupNavGraph(authService: AuthService) {
    val isLoggedIn by authService.isLoggedIn.collectAsState()
    val navController = rememberNavController()
    val gson = Gson()

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
            PlacesScreen(navController = navController)
        }
        composable("errands") {
            ErrandsScreen(navController = navController)
        }
        composable("addPlace") {
            AddEditPlaceScreen(
                existingPlace = null,
                placeService = appDependencies.placeService,
                collectionService = appDependencies.collectionService,
                onNavigateBack = {
                    navController.previousBackStackEntry?.savedStateHandle?.set("refreshPlaces", true)
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = "editPlace/{placeJson}",
            arguments = listOf(navArgument("placeJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val encoded = backStackEntry.arguments?.getString("placeJson") ?: ""
            val json = URLDecoder.decode(encoded, StandardCharsets.UTF_8.toString())
            val place = gson.fromJson(json, Place::class.java)
            AddEditPlaceScreen(
                existingPlace = place,
                placeService = appDependencies.placeService,
                collectionService = appDependencies.collectionService,
                onNavigateBack = {
                    navController.previousBackStackEntry?.savedStateHandle?.set("refreshPlaces", true)
                    navController.popBackStack()
                }
            )
        }
        composable("addErrand") {
            AddEditErrandScreen(
                existingErrand = null,
                errandService = appDependencies.errandService,
                placeService = appDependencies.placeService,
                onNavigateBack = {
                    navController.previousBackStackEntry?.savedStateHandle?.set("refreshErrands", true)
                    navController.popBackStack()
                }
            )
        }
        composable("smartRoute") {
            SmartRouteScreen()
        }
        composable("nearby") {
            NearbyScreen()
        }
        composable("settings") {
            SettingsScreen(navController = navController)
        }
        composable("collections") {
            CollectionsScreen(navController = navController)
        }
        composable("addCollection") {
            AddEditCollectionScreen(
                existingCollection = null,
                collectionService = appDependencies.collectionService,
                onNavigateBack = {
                    navController.previousBackStackEntry?.savedStateHandle?.set("refreshCollections", true)
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = "editCollection/{collectionJson}",
            arguments = listOf(navArgument("collectionJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val encoded = backStackEntry.arguments?.getString("collectionJson") ?: ""
            val json = URLDecoder.decode(encoded, StandardCharsets.UTF_8.toString())
            val collection = gson.fromJson(json, PlaceCollection::class.java)
            AddEditCollectionScreen(
                existingCollection = collection,
                collectionService = appDependencies.collectionService,
                onNavigateBack = {
                    navController.previousBackStackEntry?.savedStateHandle?.set("refreshCollections", true)
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = "editErrand/{errandJson}",
            arguments = listOf(navArgument("errandJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val encoded = backStackEntry.arguments?.getString("errandJson") ?: ""
            val json = URLDecoder.decode(encoded, StandardCharsets.UTF_8.toString())
            val errand = gson.fromJson(json, Errand::class.java)
            AddEditErrandScreen(
                existingErrand = errand,
                errandService = appDependencies.errandService,
                placeService = appDependencies.placeService,
                onNavigateBack = {
                    navController.previousBackStackEntry?.savedStateHandle?.set("refreshErrands", true)
                    navController.popBackStack()
                }
            )
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
