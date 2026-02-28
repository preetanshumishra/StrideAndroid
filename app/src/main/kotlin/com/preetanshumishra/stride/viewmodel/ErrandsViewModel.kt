package com.preetanshumishra.stride.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.preetanshumishra.stride.data.models.Errand
import com.preetanshumishra.stride.services.ErrandService
import com.preetanshumishra.stride.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ErrandsViewModel(
    private val errandService: ErrandService
) : ViewModel() {

    private val _errands = MutableStateFlow<List<Errand>>(emptyList())
    val errands: StateFlow<List<Errand>> = _errands.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _statusFilter = MutableStateFlow("all")
    val statusFilter: StateFlow<String> = _statusFilter.asStateFlow()

    private val _priorityFilter = MutableStateFlow("all")
    val priorityFilter: StateFlow<String> = _priorityFilter.asStateFlow()

    private val _filteredErrands = MutableStateFlow<List<Errand>>(emptyList())
    val filteredErrands: StateFlow<List<Errand>> = _filteredErrands.asStateFlow()

    init {
        loadErrands()
        viewModelScope.launch {
            combine(_errands, _statusFilter, _priorityFilter) { errands, status, priority ->
                errands.filter { errand ->
                    val statusMatch = status == "all" || errand.status == status
                    val priorityMatch = priority == "all" || errand.priority == priority
                    statusMatch && priorityMatch
                }
            }.collect { _filteredErrands.value = it }
        }
    }

    fun loadErrands() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            when (val result = errandService.getErrands()) {
                is Resource.Success -> { _errands.value = result.data ?: emptyList() }
                is Resource.Error -> { _errorMessage.value = result.message }
                is Resource.Loading -> {}
            }

            _isLoading.value = false
        }
    }

    fun setStatusFilter(status: String) { _statusFilter.value = status }
    fun setPriorityFilter(priority: String) { _priorityFilter.value = priority }

    fun completeErrand(id: String) {
        viewModelScope.launch {
            when (errandService.completeErrand(id)) {
                is Resource.Success -> loadErrands()
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        }
    }

    fun deleteErrand(id: String) {
        viewModelScope.launch {
            when (errandService.deleteErrand(id)) {
                is Resource.Success -> loadErrands()
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        }
    }
}
