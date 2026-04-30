package com.example.compose.geniatea.data.repository

import com.example.compose.geniatea.data.backendConection.ApiService
import com.example.compose.geniatea.data.backendConection.BackendAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocationRepository {
    private val api = BackendAPI.retrofitService

    suspend fun getLocations(token: String): Result<List<ApiService.LocationDTO>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getLocations("Bearer $token")
                if (response.isSuccessful) {
                    Result.success(response.body() ?: emptyList())
                } else {
                    Result.failure(Exception("Error getting locations: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun createLocation(token: String, name: String, latitude: Double, longitude: Double): Result<ApiService.LocationDTO> {
        return withContext(Dispatchers.IO) {
            try {
                val request = ApiService.LocationRequestDTO(name, latitude, longitude)
                val response = api.createLocation("Bearer $token", request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error creating location: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updateLocation(token: String, id: Long, name: String, latitude: Double, longitude: Double): Result<ApiService.LocationDTO> {
        return withContext(Dispatchers.IO) {
            try {
                val request = ApiService.LocationRequestDTO(name, latitude, longitude)
                val response = api.updateLocation("Bearer $token", id, request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error updating location: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteLocation(token: String, id: Long): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.deleteLocation("Bearer $token", id)
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Error deleting location: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updateCurrentLocation(token: String, latitude: Double, longitude: Double): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val request = ApiService.UpdateUserLocationDTO(latitude, longitude)
                val response = api.updateCurrentLocation("Bearer $token", request)
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Error updating current location: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
