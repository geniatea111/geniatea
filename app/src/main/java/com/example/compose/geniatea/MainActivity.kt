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

            setContent {
                val isDark by settingsViewModel.darkMode.collectAsState()
                val theme by appColorViewModel.themeVariant.collectAsState()
                val fontSize by settingsViewModel.fontSize.collectAsState()

                val fontScale = when(fontSize) {
                    "S" -> 0.85f
                    "L" -> 1.15f
                    else -> 1.0f
                }

                Log.d("MainActivity", "Applying theme: $theme, Dark mode: $isDark, Font Scale: $fontScale")

                GenIATEATheme(
                    themeVariant = theme,
                    isDarkTheme = isDark,
                    fontScale = fontScale
                ) {
                    // Inflate your XML layout with Compose support
                    AndroidViewBinding(ContentMainBinding::inflate) {
                        if (isUserLoggedIn && savedInstanceState == null) {
                            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
                            // Using runBlocking here for a quick check, ensuring it doesn't block significantly
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

            // Perform network sync in parallel/background AFTER UI is set
            if (isUserLoggedIn) {
                launch {
                    val token = dataStore.getToken()
                    if (!token.isNullOrBlank()) {
                        try {
                            // Validate token. AuthInterceptor will handle refresh if needed.
                            // If it returns 401, it means both access and refresh tokens failed.
                            // This runs in the background now, allowing UI to show up.
                            val response = BackendAPI.retrofitService.getUserPreferences("Bearer $token")
                            if (response.isSuccessful) {
                                val prefs = response.body()
                                if (prefs != null) {
                                    // Sync voice settings
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
                                // If we logout here, the UI will likely react if it observes isUserLoggedIn, 
                                // or we might need to trigger navigation. 
                                // But since isUserLoggedIn was true initially, the user is likely on Home.
                                // The StoreDataUser.logoutUser() should trigger flows that might handle this,
                                // or the user will be logged out on next restart.
                            }
                        } catch (e: Exception) {
                            Log.e("MainActivity", "Token validation error (fail-open): ${e.message}")
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
}