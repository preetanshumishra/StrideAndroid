package com.preetanshumishra.stride.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.preetanshumishra.stride.data.models.PlaceCollection
import com.preetanshumishra.stride.services.CollectionService
import com.preetanshumishra.stride.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CollectionsViewModel(
    private val collectionService: CollectionService
) : ViewModel() {

    private val _collections = MutableStateFlow<List<PlaceCollection>>(emptyList())
    val collections: StateFlow<List<PlaceCollection>> = _collections.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init { loadCollections() }

    fun loadCollections() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            when (val result = collectionService.getCollections()) {
                is Resource.Success -> _collections.value = result.data ?: emptyList()
                is Resource.Error -> _errorMessage.value = result.message
                is Resource.Loading -> {}
            }
            _isLoading.value = false
        }
    }

    fun deleteCollection(id: String) {
        viewModelScope.launch {
            when (collectionService.deleteCollection(id)) {
                is Resource.Success -> _collections.value = _collections.value.filter { it.id != id }
                is Resource.Error -> _errorMessage.value = "Failed to delete collection"
                is Resource.Loading -> {}
            }
        }
    }
}
