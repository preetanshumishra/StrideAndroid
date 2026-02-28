package com.preetanshumishra.stride.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.preetanshumishra.stride.data.models.Place
import com.preetanshumishra.stride.data.models.PlaceCollection
import com.preetanshumishra.stride.di.appDependencies
import com.preetanshumishra.stride.services.CollectionService
import com.preetanshumishra.stride.services.PlaceService
import com.preetanshumishra.stride.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class PlacesViewModel(
    private val placeService: PlaceService,
    private val collectionService: CollectionService
) : ViewModel() {

    private val _places = MutableStateFlow<List<Place>>(emptyList())
    val places: StateFlow<List<Place>> = _places.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filteredPlaces = MutableStateFlow<List<Place>>(emptyList())
    val filteredPlaces: StateFlow<List<Place>> = _filteredPlaces.asStateFlow()

    private val _collectionFilter = MutableStateFlow<String?>(null)
    val collectionFilter: StateFlow<String?> = _collectionFilter.asStateFlow()

    private val _collections = MutableStateFlow<List<PlaceCollection>>(emptyList())
    val collections: StateFlow<List<PlaceCollection>> = _collections.asStateFlow()

    init {
        loadPlaces()
        loadCollections()
        viewModelScope.launch {
            combine(_places, _searchQuery, _collectionFilter) { places, query, colFilter ->
                var result = if (colFilter != null) places.filter { it.collectionId == colFilter } else places
                if (query.isNotBlank()) {
                    result = result.filter {
                        it.name.contains(query, ignoreCase = true) ||
                        it.address.contains(query, ignoreCase = true) ||
                        (it.category?.contains(query, ignoreCase = true) == true)
                    }
                }
                result
            }.collect { _filteredPlaces.value = it }
        }
    }

    fun loadPlaces() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            when (val result = placeService.getPlaces()) {
                is Resource.Success -> {
                    val places = result.data ?: emptyList()
                    _places.value = places
                    appDependencies.woosmapManager.registerGeofences(places)
                }
                is Resource.Error -> { _errorMessage.value = result.message }
                is Resource.Loading -> {}
            }

            _isLoading.value = false
        }
    }

    fun loadCollections() {
        viewModelScope.launch {
            when (val result = collectionService.getCollections()) {
                is Resource.Success -> { _collections.value = result.data ?: emptyList() }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCollectionFilter(id: String?) {
        _collectionFilter.value = id
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

    fun recordVisit(id: String) {
        viewModelScope.launch {
            placeService.recordVisit(id)
            loadPlaces()
        }
    }
}
