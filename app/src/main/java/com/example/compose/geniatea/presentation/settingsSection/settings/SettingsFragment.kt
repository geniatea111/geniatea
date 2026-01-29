package com.example.compose.geniatea.presentation.settingsSection.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.compose.geniatea.R
import com.example.compose.geniatea.presentation.settingsSection.preferencesSettings.PreferencesAction
import com.example.compose.geniatea.theme.GenIATEATheme
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.getValue
import com.example.compose.geniatea.presentation.settingsSection.appColor.AppColorViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue


class SettingsFragment : Fragment() {
    private val viewModel: SettingsViewModel by activityViewModels()
    private val appColorViewModel: AppColorViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.fragment_profile, container, false)

        Log.i("SettinsFragment", "DataStore initialized: ${viewModel.state.value.isDarkMode}")

        rootView.findViewById<ComposeView>(R.id.toolbar_compose_view).apply {

            viewModel.actionEvent.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { action ->
                    when (action) {
                        is SettingsAction.OnBackPressed -> activity?.onBackPressedDispatcher?.onBackPressed()
                        is SettingsAction.OnLogoutPressed -> {
                            lifecycleScope.launch {
                                viewModel.logoutUser()
                            }
                            findNavController().navigate(R.id.nav_prelogin)
                        }
                        is SettingsAction.OnAboutPressed -> {
                            findNavController().navigate(R.id.nav_about)
                        }
                        is SettingsAction.OnPrivacyPolicyPressed -> {
                            findNavController().navigate(R.id.nav_privacy)
                        }
                        is SettingsAction.OnLocalizationsPressed -> {
                            findNavController().navigate(R.id.nav_location)
                        }
                        is SettingsAction.OnAISettingsPressed -> {
                           findNavController().navigate(R.id.nav_ai_settings)
                        }
                        is SettingsAction.OnAccountPressed -> {
                            findNavController().navigate(R.id.nav_account)
                        }

                        is SettingsAction.OnNotificationPress -> {
                            val intent = Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                            }
                            startActivity(intent)
                        }

                        is SettingsAction.OnLanguagePress -> {
                            AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(Locale.forLanguageTag(languagesTag(action.language))))
                        }

                        is SettingsAction.OnDarkModeToggle -> {
                            viewLifecycleOwner.lifecycleScope.launch {
                                viewModel.setDarkModeState(action.isChecked)
                            }
                        }

                        is SettingsAction.OnAnimationsToggle -> {
                            viewLifecycleOwner.lifecycleScope.launch {
                                viewModel.setAnimationsEnabled(action.isChecked)
                            }
                        }

                        is SettingsAction.OnAppIconPressed -> {
                            findNavController().navigate(R.id.nav_app_icon)
                        }
                        is SettingsAction.OnAppColorPressed -> {
                            findNavController().navigate(R.id.nav_app_color)
                        }

                        else -> {
                            // Handle other actions if needed
                        }
                    }
                }
            }

        }

        rootView.findViewById<ComposeView>(R.id.compose_view).apply {
            setContent {
                val fontSize by viewModel.fontSize.collectAsState()
                val isDark by viewModel.darkMode.collectAsState()
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
                    SettingsRoot(
                        viewModel = viewModel,
                        onBackPressed = {
                            activity?.onBackPressedDispatcher?.onBackPressed()
                        }
                    )
                }
            }
        }
        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUserData()
    }

    fun languagesTag(language: String): String {
        return when (language) {
            "Español" -> "es"
            "English" -> "en"
            "Français" -> "fr"
            else -> "es"
        }
    }
}