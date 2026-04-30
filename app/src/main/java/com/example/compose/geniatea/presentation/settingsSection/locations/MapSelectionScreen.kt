package com.example.compose.geniatea.presentation.settingsSection.locations

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.compose.geniatea.R
import com.example.compose.geniatea.data.backendConection.ApiService.LocationDTO
import com.example.compose.geniatea.presentation.components.TitleAppBar
import com.example.compose.geniatea.theme.sdp
import com.example.compose.geniatea.utils.LocationTracker
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSelectionScreen(
    locationToEdit: LocationDTO?,
    viewModel: LocationViewModel,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // The OSMDroid configuration is now managed globally in MainActivity

    var name by remember { mutableStateOf(locationToEdit?.name ?: "") }
    var selectedLocation by remember { 
        mutableStateOf(
            if (locationToEdit != null) GeoPoint(locationToEdit.latitude, locationToEdit.longitude)
            else GeoPoint(40.416775, -3.703790) // Default to Madrid or user's current location later
        )
    }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                               permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    // Reference to update map later if needed
    var mapReference by remember { mutableStateOf<MapView?>(null) }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        } else if (locationToEdit == null) {
            val tracker = LocationTracker(context)
            val currLoc = tracker.getCurrentLocation()
            if (currLoc != null) {
                selectedLocation = GeoPoint(currLoc.latitude, currLoc.longitude)
                mapReference?.controller?.setCenter(selectedLocation)
                mapReference?.controller?.setZoom(15.0)
            }
        }
    }

    Scaffold(
        topBar = {
            TitleAppBar(
                title = if (locationToEdit != null) stringResource(R.string.locations_edit_title) else stringResource(R.string.locations_new_title),
                onNavIconPressed = onBackPressed
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.locations_name_label)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.sdp())
            )

            Box(modifier = Modifier.weight(1f).clipToBounds()) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->

                        MapView(ctx).apply {
                            mapReference = this
                            
                            android.util.Log.d("MapSelectionScreen", "Iniciando MapView...")
                            android.util.Log.d("MapSelectionScreen", "User-Agent actual: ${Configuration.getInstance().userAgentValue}")
                            
                            // Usamos el servidor de mapas de CARTO (Light All) que es gratuito, no requiere API key y no tiene las restricciones estrictas de OSM o Wikimedia.
                            val cartoLightSource = org.osmdroid.tileprovider.tilesource.XYTileSource(
                                "CartoLight",
                                0,
                                20,
                                256,
                                ".png",
                                arrayOf(
                                    "https://a.basemaps.cartocdn.com/light_all/",
                                    "https://b.basemaps.cartocdn.com/light_all/",
                                    "https://c.basemaps.cartocdn.com/light_all/"
                                ),
                                "© OpenStreetMap contributors, © CARTO"
                            )
                            setTileSource(cartoLightSource)
                            setMultiTouchControls(true)
                            
                            android.util.Log.d("MapSelectionScreen", "Configurando zoom 15.0 y centrando en Lat: ${selectedLocation.latitude}, Lon: ${selectedLocation.longitude}")
                            controller.setZoom(15.0)
                            controller.setCenter(selectedLocation)

                            val mReceive = object : MapEventsReceiver {
                                override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                                    p?.let {
                                        android.util.Log.d("MapSelectionScreen", "Usuario tocó el mapa en Lat: ${it.latitude}, Lon: ${it.longitude}")
                                        selectedLocation = it
                                        overlays.clear()
                                        val newMarker = Marker(this@apply)
                                        newMarker.position = it
                                        newMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                        newMarker.title = "Ubicación Seleccionada"
                                        overlays.add(MapEventsOverlay(this))
                                        overlays.add(newMarker)
                                        invalidate()
                                        android.util.Log.d("MapSelectionScreen", "Marcador actualizado y mapa invalidado para redibujar")
                                    }
                                    return true
                                }
                                override fun longPressHelper(p: GeoPoint?): Boolean = false
                            }
                            
                            val startMarker = Marker(this)
                            startMarker.position = selectedLocation
                            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            startMarker.title = "Ubicación Seleccionada"
                            
                            overlays.add(MapEventsOverlay(mReceive))
                            overlays.add(startMarker)
                            android.util.Log.d("MapSelectionScreen", "Marcador inicial colocado.")
                        }
                    },
                    update = { view ->
                        // Optional updates if selectedLocation changes externally
                    }
                )
            }

            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        viewModel.saveLocation(
                            id = locationToEdit?.id,
                            name = name,
                            latitude = selectedLocation.latitude,
                            longitude = selectedLocation.longitude
                        )
                        onBackPressed()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.sdp()),
                enabled = name.isNotBlank()
            ) {
                Text(stringResource(R.string.locations_save))
            }
        }
    }
}
