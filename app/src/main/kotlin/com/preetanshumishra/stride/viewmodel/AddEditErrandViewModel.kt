package com.preetanshumishra.stride.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.preetanshumishra.stride.data.models.Errand
import com.preetanshumishra.stride.data.models.ErrandRequest
import com.preetanshumishra.stride.data.models.Place
import com.preetanshumishra.stride.data.models.RecurringRequest
import com.preetanshumishra.stride.services.ErrandService
import com.preetanshumishra.stride.services.PlaceService
import com.preetanshumishra.stride.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddEditErrandViewModel(
    private val errandService: ErrandService,
    val existingErrand: Errand? = null,
    private val placeService: PlaceService
) : ViewModel() {

    var title = MutableStateFlow(existingErrand?.title ?: "")
    var category = MutableStateFlow(existingErrand?.category ?: "")
    var priority = MutableStateFlow(existingErrand?.priority ?: "medium")
    var deadline = MutableStateFlow(existingErrand?.deadline ?: "")
    var linkedPlaceId = MutableStateFlow(existingErrand?.linkedPlaceId ?: "")
    var isRecurring = MutableStateFlow(existingErrand?.recurring?.enabled ?: false)
    var recurringInterval = MutableStateFlow(existingErrand?.recurring?.intervalDays ?: 7)
    var recurringUnit = MutableStateFlow("days")

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    private val _places = MutableStateFlow<List<Place>>(emptyList())
    val places: StateFlow<List<Place>> = _places.asStateFlow()

    val isEditing = existingErrand != null

    init {
        loadPlaces()
    }

    private fun loadPlaces() {
        viewModelScope.launch {
            val result = placeService.getPlaces()
            _places.value = if (result is Resource.Success) result.data ?: emptyList() else emptyList()
        }
    }

    fun save() {
        val titleVal = title.value.trim()
        if (titleVal.isEmpty()) { _errorMessage.value = "Title is required"; return }

        val recurring = if (isRecurring.value) {
            RecurringRequest(interval = recurringInterval.value, unit = recurringUnit.value)
        } else null

        val request = ErrandRequest(
            title = titleVal,
            category = category.value.trim().ifEmpty { null },
            priority = priority.value,
            deadline = deadline.value.trim().ifEmpty { null },
            linkedPlaceId = linkedPlaceId.value.trim().ifEmpty { null },
            recurring = recurring
        )

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = if (existingErrand != null) {
                errandService.updateErrand(existingErrand.id, request)
            } else {
                errandService.createErrand(request)
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
