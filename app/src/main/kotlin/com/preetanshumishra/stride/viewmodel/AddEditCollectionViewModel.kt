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

class AddEditCollectionViewModel(
    private val collectionService: CollectionService,
    val existingCollection: PlaceCollection? = null
) : ViewModel() {

    val name = MutableStateFlow(existingCollection?.name ?: "")

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _didSave = MutableStateFlow(false)
    val didSave: StateFlow<Boolean> = _didSave.asStateFlow()

    val isEditing get() = existingCollection != null

    fun save() {
        val nameVal = name.value.trim()
        if (nameVal.isEmpty()) { _errorMessage.value = "Name is required"; return }
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = if (existingCollection != null) {
                collectionService.updateCollection(existingCollection.id, nameVal)
            } else {
                collectionService.createCollection(nameVal)
            }
            when (result) {
                is Resource.Success -> _didSave.value = true
                is Resource.Error -> _errorMessage.value = result.message ?: "Failed to save"
                is Resource.Loading -> {}
            }
            _isLoading.value = false
        }
    }
}
