package com.preetanshumishra.stride.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.preetanshumishra.stride.di.appDependencies

class ViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val vm: ViewModel = when (modelClass) {
            LoginViewModel::class.java -> LoginViewModel(appDependencies.authService)
            RegisterViewModel::class.java -> RegisterViewModel(appDependencies.authService)
            HomeViewModel::class.java -> HomeViewModel(appDependencies.authService)
            PlacesViewModel::class.java -> PlacesViewModel(appDependencies.placeService)
            ErrandsViewModel::class.java -> ErrandsViewModel(appDependencies.errandService)
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
        return vm as T
    }
}
