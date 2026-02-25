package com.preetanshumishra.stride.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.preetanshumishra.stride.data.models.Place
import com.preetanshumishra.stride.services.PlaceService
import com.preetanshumishra.stride.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlacesViewModel(
    private val placeService: PlaceService
) : ViewModel() {

    private val _places = MutableStateFlow<List<Place>>(emptyList())
    val places: StateFlow<List<Place>> = _places.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadPlaces()
    }

    fun loadPlaces() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            when (val result = placeService.getPlaces()) {
                is Resource.Success -> { _places.value = result.data ?: emptyList() }
                is Resource.Error -> { _errorMessage.value = result.message }
                is Resource.Loading -> {}
            }

            _isLoading.value = false
        }
    }

    fun deletePlace(id: String) {
        viewModelScope.launch {
            when (placeService.deletePlace(id)) {
                is Resource.Success -> loadPlaces()
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        }
    }
}
