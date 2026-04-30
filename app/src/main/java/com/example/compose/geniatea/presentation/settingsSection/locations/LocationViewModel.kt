package com.example.compose.geniatea.presentation.settingsSection.locations

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.geniatea.data.StoreDataUser
import com.example.compose.geniatea.data.backendConection.ApiService.LocationDTO
import com.example.compose.geniatea.data.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LocationState(
    val isLoading: Boolean = false,
    val locations: List<LocationDTO> = emptyList(),
    val error: String? = null
)

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStore = StoreDataUser(application.applicationContext)
    private val locationRepository: LocationRepository = LocationRepository()

    private val _state = MutableStateFlow(LocationState())
    val state: StateFlow<LocationState> = _state.asStateFlow()

    init {
        loadLocations()
    }

    fun loadLocations() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val token = dataStore.getToken()
            if (!token.isNullOrBlank()) {
                val result = locationRepository.getLocations(token)
                if (result.isSuccess) {
                    _state.update { it.copy(isLoading = false, locations = result.getOrNull() ?: emptyList()) }
                } else {
                    _state.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message) }
                }
            } else {
                _state.update { it.copy(isLoading = false, error = "User not authenticated") }
            }
        }
    }

    fun saveLocation(id: Long?, name: String, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val token = dataStore.getToken()
            if (!token.isNullOrBlank()) {
                val result = if (id == null) {
                    locationRepository.createLocation(token, name, latitude, longitude)
                } else {
                    locationRepository.updateLocation(token, id, name, latitude, longitude)
                }
                
                if (result.isSuccess) {
                    loadLocations() // Reload after success
                } else {
                    _state.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message) }
                }
            }
        }
    }

    fun deleteLocation(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val token = dataStore.getToken()
            if (!token.isNullOrBlank()) {
                val result = locationRepository.deleteLocation(token, id)
                if (result.isSuccess) {
                    loadLocations() // Reload after success
                } else {
                    _state.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message) }
                }
            }
        }
    }
}
