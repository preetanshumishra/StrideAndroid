package com.preetanshumishra.stride.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.preetanshumishra.stride.data.models.Errand
import com.preetanshumishra.stride.services.RouteService
import com.preetanshumishra.stride.utils.LocationHelper
import com.preetanshumishra.stride.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SmartRouteViewModel(
    private val routeService: RouteService,
    private val locationHelper: LocationHelper
) : ViewModel() {

    private val _routedErrands = MutableStateFlow<List<Errand>>(emptyList())
    val routedErrands: StateFlow<List<Errand>> = _routedErrands.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _needsPermission = MutableStateFlow(false)
    val needsPermission: StateFlow<Boolean> = _needsPermission.asStateFlow()

    fun fetchRoute() {
        if (!locationHelper.hasLocationPermission()) {
            _needsPermission.value = true
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val location = locationHelper.getCurrentLocation()
                when (val result = routeService.getRoute(location.latitude, location.longitude)) {
                    is Resource.Success -> _routedErrands.value = result.data!!
                    is Resource.Error -> _errorMessage.value = result.message
                    is Resource.Loading -> {}
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to get location"
            }
            _isLoading.value = false
        }
    }

    fun permissionGranted() {
        _needsPermission.value = false
        fetchRoute()
    }
}
