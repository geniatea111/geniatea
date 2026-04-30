package com.example.compose.geniatea.presentation.settingsSection.locationSettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.compose.geniatea.R
import com.example.compose.geniatea.theme.GenIATEATheme
import com.example.compose.geniatea.presentation.settingsSection.settings.SettingsViewModel
import com.example.compose.geniatea.presentation.settingsSection.appColor.AppColorViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.activity.compose.BackHandler
import com.example.compose.geniatea.data.backendConection.ApiService.LocationDTO
import com.example.compose.geniatea.presentation.settingsSection.locations.LocationViewModel
import com.example.compose.geniatea.presentation.settingsSection.locations.LocationsScreen
import com.example.compose.geniatea.presentation.settingsSection.locations.MapSelectionScreen

class LocationFragment : Fragment() {
    private val viewModel: LocationViewModel by activityViewModels()
    private val settingsViewModel: SettingsViewModel by activityViewModels()
    private val appColorViewModel: AppColorViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.fragment_profile, container, false)

        rootView.findViewById<ComposeView>(R.id.compose_view).apply {
            setContent {
                val fontSize by settingsViewModel.fontSize.collectAsState()
                val isDark by settingsViewModel.darkMode.collectAsState()
                val themeVariant by appColorViewModel.themeVariant.collectAsState()

                val fontScale = when(fontSize) {
                    "S" -> 0.85f
                    "L" -> 1.15f
                    else -> 1.0f
                }

                GenIATEATheme(
                    themeVariant = themeVariant,
                    isDarkTheme = isDark,
                    fontScale = fontScale
                ) {
                    var showMap by remember { mutableStateOf(false) }
                    var locationToEdit by remember { mutableStateOf<LocationDTO?>(null) }

                    if (showMap) {
                        BackHandler {
                            showMap = false
                            locationToEdit = null
                        }
                        MapSelectionScreen(
                            locationToEdit = locationToEdit,
                            viewModel = viewModel,
                            onBackPressed = {
                                showMap = false
                                locationToEdit = null
                            }
                        )
                    } else {
                        LocationsScreen(
                            viewModel = viewModel,
                            onBackPressed = {
                                activity?.onBackPressedDispatcher?.onBackPressed()
                            },
                            onNavigateToMap = { location ->
                                locationToEdit = location
                                showMap = true
                            }
                        )
                    }
                }
            }
        }
        return rootView
    }
}