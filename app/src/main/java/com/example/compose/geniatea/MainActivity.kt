/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.geniatea

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.compose.geniatea.data.StoreDataUser
import com.example.compose.geniatea.databinding.ContentMainBinding
import kotlinx.coroutines.launch
import com.example.compose.geniatea.data.backendConection.BackendAPI
import com.example.compose.geniatea.presentation.settingsSection.settings.SettingsViewModel
import com.example.compose.geniatea.theme.GenIATEATheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.material3.CircularProgressIndicator
import com.example.compose.geniatea.presentation.settingsSection.appColor.AppColorViewModel
import kotlinx.coroutines.flow.first

/**
 * Main activity for the app.
 */

class MainActivity : AppCompatActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()
    private val appColorViewModel : AppColorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets -> insets }
        BackendAPI.init(this)

        // Request POST_NOTIFICATIONS permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICATION_PERMISSION
                )
            }
        }

        // Load saved theme and dark mode before setting content
        lifecycleScope.launch {
            val dataStore = StoreDataUser(this@MainActivity)
            val isDarkMode = dataStore.getDarkMode().first()
            var isUserLoggedIn: Boolean = dataStore.isUserLoggedIn.first()

            val themeVariant = dataStore.getThemeVariant().first()

            // Save values into ViewModel
            settingsViewModel.setDarkModeState(isDarkMode)
            appColorViewModel.setThemeVariant(themeVariant)

            // Apply system dark mode
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )

            // Observe global logout
            launch {
                dataStore.isUserLoggedIn.collect { loggedIn ->
                    if (!loggedIn) {
                        try {
                            val navController = findNavController()
                            if (navController.currentDestination?.id != R.id.nav_prelogin &&
                                navController.currentDestination?.id != R.id.nav_login &&
                                navController.currentDestination?.id != R.id.nav_splash_screen) {
                                navController.navigate(R.id.nav_prelogin) {
                                    popUpTo(R.id.mobile_navigation) { inclusive = true }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("MainActivity", "Navigation error: ${e.message}")
                        }
                    }
                }
            }
            // Limpiar cache de osmdroid completo SOLO UNA VEZ para solucionar el problema de las imágenes "Access Blocked"
            try {
                val prefs = getSharedPreferences("osmdroid_fix", android.content.Context.MODE_PRIVATE)
                val isFixed = prefs.getBoolean("cache_cleared_v3", false)
                
                Log.d("OSMDroidConfig", "Estado de cache_cleared_v3: $isFixed")
                
                if (!isFixed) {
                    Log.d("OSMDroidConfig", "Procediendo a borrar la carpeta osmdroid para forzar limpieza...")
                    // 1. Borramos toda la carpeta de osmdroid (incluye base de datos SQLite de caché)
                    val osmdroidBasePath = java.io.File(filesDir, "osmdroid")
                    if (osmdroidBasePath.exists()) {
                        val deleted = osmdroidBasePath.deleteRecursively()
                        Log.d("OSMDroidConfig", "Resultado de borrar osmdroidBasePath: $deleted")
                    } else {
                        Log.d("OSMDroidConfig", "La carpeta osmdroid no existía.")
                    }
                    prefs.edit().putBoolean("cache_cleared_v3", true).apply()
                    Log.d("OSMDroidConfig", "Se marcó cache_cleared_v3 como true")
                }
                
                // 2. Configuramos OSMDroid con un User-Agent válido según las políticas de OSM
                Log.d("OSMDroidConfig", "Aplicando configuración global de OSMDroid...")
                org.osmdroid.config.Configuration.getInstance().apply {
                    load(applicationContext, getSharedPreferences("osmdroid", android.content.Context.MODE_PRIVATE))
                    // IMPORTANTE: OpenStreetMap bloquea los user-agents que empiezan por "com.example"
                    val newUserAgent = "GenIATEA_App/1.0 (contact@geniatea.com) Android"
                    userAgentValue = newUserAgent
                    
                    // Habilitar logs internos de OSMDroid para ver peticiones HTTP
                    isDebugMode = true
                    isDebugMapTileDownloader = true
                    isDebugTileProviders = true
                    
                    Log.d("OSMDroidConfig", "User-Agent configurado exitosamente a: $newUserAgent")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error clearing OSMDroid cache", e)
            }

            setContent {
                val isDark by settingsViewModel.darkMode.collectAsState()
                val theme by appColorViewModel.themeVariant.collectAsState()
                val fontSize by settingsViewModel.fontSize.collectAsState()

                val fontScale = when(fontSize) {
                    "S" -> 0.85f
                    "L" -> 1.15f
                    else -> 1.0f
                }

                var isCheckingToken by remember { mutableStateOf(isUserLoggedIn) }

                val context = androidx.compose.ui.platform.LocalContext.current
                val locationPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
                    contract = androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions(),
                    onResult = { permissions ->
                        val fineLocationGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false
                        val coarseLocationGranted = permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
                        if (fineLocationGranted || coarseLocationGranted) {
                            lifecycleScope.launch {
                                val token = dataStore.getToken()
                                if (!token.isNullOrBlank()) {
                                    val tracker = com.example.compose.geniatea.utils.LocationTracker(context)
                                    val location = tracker.getCurrentLocation()
                                    if (location != null) {
                                        val repo = com.example.compose.geniatea.data.repository.LocationRepository()
                                        repo.updateCurrentLocation(token, location.latitude, location.longitude)
                                    }
                                }
                            }
                        }
                    }
                )

                LaunchedEffect(Unit) {
                    if (isUserLoggedIn) {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                android.Manifest.permission.ACCESS_FINE_LOCATION,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )

                        val token = dataStore.getToken()
                        if (!token.isNullOrBlank()) {
                            try {
                                val response = BackendAPI.retrofitService.getUserPreferences("Bearer $token")
                                if (response.isSuccessful) {
                                    val prefs = response.body()
                                    if (prefs != null) {
                                        if (prefs.continuousVoice != null) {
                                            dataStore.saveContinuousVoiceMode(prefs.continuousVoice)
                                        }
                                        if (prefs.voiceKeyword != null) {
                                            dataStore.saveContinuousVoiceKeyword(prefs.voiceKeyword)
                                        }
                                    }
                                } else {
                                    Log.w("MainActivity", "Token invalidation or user check failed (code: ${response.code()}). Logging out.")
                                    dataStore.logoutUser()
                                }
                            } catch (e: Exception) {
                                Log.e("MainActivity", "Token validation error (fail-open): ${e.message}")
                                dataStore.logoutUser()
                            }
                        }
                    }
                    isCheckingToken = false
                }

                Log.d("MainActivity", "Applying theme: $theme, Dark mode: $isDark, Font Scale: $fontScale")

                GenIATEATheme(
                    themeVariant = theme,
                    isDarkTheme = isDark,
                    fontScale = fontScale
                ) {
                    if (isCheckingToken) {
                        androidx.compose.material3.Surface(modifier = Modifier.fillMaxSize()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    } else {
                        // Inflate your XML layout with Compose support
                        AndroidViewBinding(ContentMainBinding::inflate) {
                            // Re-check logic here since isUserLoggedIn might have changed to false after token check
                            val currentLoggedIn = kotlinx.coroutines.runBlocking { dataStore.isUserLoggedIn.first() }
                            if (currentLoggedIn && savedInstanceState == null) {
                                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
                                val isOnboardingCompleted = kotlinx.coroutines.runBlocking { dataStore.getOnboardingCompleted().first() }

                                navHostFragment?.navController?.let { navController ->
                                    if (isOnboardingCompleted) {
                                        if (navController.currentDestination?.id != R.id.nav_home) {
                                            navController.navigate(R.id.nav_home)
                                        }
                                    } else {
                                         if (navController.currentDestination?.id != R.id.nav_onboarding) {
                                            navController.navigate(R.id.nav_onboarding)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController().navigateUp() || super.onSupportNavigateUp()
    }

    private fun findNavController(): NavController {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }

    companion object {
        private const val REQUEST_NOTIFICATION_PERMISSION = 1001
    }
}