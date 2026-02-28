package com.preetanshumishra.stride.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.preetanshumishra.stride.data.models.Errand
import com.preetanshumishra.stride.data.models.Place
import com.preetanshumishra.stride.services.NearbyService
import com.preetanshumishra.stride.utils.LocationHelper
import com.preetanshumishra.stride.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NearbyViewModel(
    private val nearbyService: NearbyService,
    private val locationHelper: LocationHelper
) : ViewModel() {

    private val _nearbyPlaces = MutableStateFlow<List<Place>>(emptyList())
    val nearbyPlaces: StateFlow<List<Place>> = _nearbyPlaces.asStateFlow()

    private val _linkedErrands = MutableStateFlow<List<Errand>>(emptyList())
    val linkedErrands: StateFlow<List<Errand>> = _linkedErrands.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _needsPermission = MutableStateFlow(false)
    val needsPermission: StateFlow<Boolean> = _needsPermission.asStateFlow()

    var radiusKm: Double = 1.0

    fun fetchNearby() {
        if (!locationHelper.hasLocationPermission()) {
            _needsPermission.value = true
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val location = locationHelper.getCurrentLocation()
                when (val result = nearbyService.getNearby(location.latitude, location.longitude, radiusKm)) {
                    is Resource.Success -> {
                        _nearbyPlaces.value = result.data!!.nearbyPlaces
                        _linkedErrands.value = result.data.linkedErrands
                    }
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
        fetchNearby()
    }
}
