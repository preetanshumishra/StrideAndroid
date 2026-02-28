package com.preetanshumishra.stride.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.preetanshumishra.stride.data.models.Place
import com.preetanshumishra.stride.data.models.PlaceCollection
import com.preetanshumishra.stride.data.models.PlaceRequest
import com.preetanshumishra.stride.services.CollectionService
import com.preetanshumishra.stride.services.PlaceService
import com.preetanshumishra.stride.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddEditPlaceViewModel(
    private val placeService: PlaceService,
    val existingPlace: Place? = null,
    private val collectionService: CollectionService
) : ViewModel() {

    var name = MutableStateFlow(existingPlace?.name ?: "")
    var address = MutableStateFlow(existingPlace?.address ?: "")
    var latitudeText = MutableStateFlow(existingPlace?.latitude?.toString() ?: "")
    var longitudeText = MutableStateFlow(existingPlace?.longitude?.toString() ?: "")
    var category = MutableStateFlow(existingPlace?.category ?: "")
    var notes = MutableStateFlow(existingPlace?.notes ?: "")
    var personalRating = MutableStateFlow(existingPlace?.personalRating ?: 0)
    val tagsText = MutableStateFlow(existingPlace?.tags?.joinToString(", ") ?: "")
    val collectionId = MutableStateFlow(existingPlace?.collectionId ?: "")

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    private val _collections = MutableStateFlow<List<PlaceCollection>>(emptyList())
    val collections: StateFlow<List<PlaceCollection>> = _collections.asStateFlow()

    val isEditing = existingPlace != null

    init {
        loadCollections()
    }

    private fun loadCollections() {
        viewModelScope.launch {
            val result = collectionService.getCollections()
            if (result is Resource.Success) {
                _collections.value = result.data ?: emptyList()
            }
        }
    }

    fun save() {
        val nameVal = name.value.trim()
        val addressVal = address.value.trim()
        val latVal = latitudeText.value.trim().toDoubleOrNull()
        val lonVal = longitudeText.value.trim().toDoubleOrNull()

        if (nameVal.isEmpty()) { _errorMessage.value = "Name is required"; return }
        if (addressVal.isEmpty()) { _errorMessage.value = "Address is required"; return }
        if (latVal == null || lonVal == null) { _errorMessage.value = "Valid latitude and longitude are required"; return }

        val tags = tagsText.value.split(",").map { it.trim() }.filter { it.isNotEmpty() }

        val request = PlaceRequest(
            name = nameVal,
            address = addressVal,
            latitude = latVal,
            longitude = lonVal,
            category = category.value.trim().ifEmpty { null },
            notes = notes.value.trim().ifEmpty { null },
            personalRating = personalRating.value.takeIf { it > 0 },
            source = existingPlace?.source ?: "manual",
            tags = tags.ifEmpty { null },
            collectionId = collectionId.value.ifBlank { null }
        )

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = if (existingPlace != null) {
                placeService.updatePlace(existingPlace.id, request)
            } else {
                placeService.createPlace(request)
            }

            when (result) {
                is Resource.Success -> _isSaved.value = true
                is Resource.Error -> _errorMessage.value = result.message
                is Resource.Loading -> {}
            }

            _isLoading.value = false
        }
    }
}
