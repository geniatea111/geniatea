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
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.compose.geniatea.data.StoreDataUser
import com.example.compose.geniatea.databinding.ContentMainBinding
import kotlinx.coroutines.launch
import android.util.Log
import com.example.compose.geniatea.data.backendConection.BackendAPI
import com.example.compose.geniatea.presentation.settingsSection.preferencesSettings.PreferencesViewModel

/**
 * Main activity for the app.
 */

class MainActivity : AppCompatActivity() {
    val preferencesViewModel : PreferencesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets -> insets }

        BackendAPI.init(this)

        setContentView(
            ComposeView(this).apply {
                consumeWindowInsets = false
                setContent {
                    AndroidViewBinding(ContentMainBinding::inflate)
                }
            },
        )

        lifecycleScope.launch {
            val isDarkMode =  StoreDataUser().getDarkMode(this@MainActivity)
            preferencesViewModel.setDarkMode(isDarkMode, this@MainActivity)

            Log.i("NavActivityTag", "DataStore initialized: $isDarkMode")

            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController().navigateUp() || super.onSupportNavigateUp()
    }

    /**
     * See https://issuetracker.google.com/142847973
     */
    private fun findNavController(): NavController {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }
}
