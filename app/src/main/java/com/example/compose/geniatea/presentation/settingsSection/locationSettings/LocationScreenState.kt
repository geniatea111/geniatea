package com.example.compose.geniatea.presentation.settingsSection.locationSettings

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.toMutableStateList

class LocationScreenState(initialLocations: List<Location>) {

    private val _locations : MutableList<Location> = initialLocations.toMutableStateList()
    val locations: List<Location> = _locations

    fun addLocation(loc: Location) {
        _locations.add(loc) // Add to the beginning of the list
    }

}


@Immutable
data class Location(
    val location: String = "",
    val name : String = "",
    val errorMessage: String? = null
)