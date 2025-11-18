package com.example.compose.geniatea.presentation.settingsSection.preferencesSettings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.compose.geniatea.R
import com.example.compose.geniatea.theme.GenIATEATheme
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.getValue

class PreferencesFragment : Fragment() {
    private val viewModel: PreferencesViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.fragment_profile, container, false)

        rootView.findViewById<ComposeView>(R.id.toolbar_compose_view).apply {
            viewModel.actionEvent.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { action ->
                    when (action) {
                        PreferencesAction.OnBackPressed ->
                            activity?.onBackPressedDispatcher?.onBackPressed()
                        is PreferencesAction.OnDarkModeToggle -> {
                            viewLifecycleOwner.lifecycleScope.launch {
                                viewModel.setDarkMode(action.isChecked, requireContext())
                            }
                        }

                        is PreferencesAction.OnNotificationPress -> {
                            val intent = Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                            }
                            startActivity(intent)
                        }

                        is PreferencesAction.OnLanguagePress -> {
                            AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(Locale.forLanguageTag(languagesTag(action.language))))
                        }

                        else -> {
                            // No action
                        }
                    }
                }
            }

        }

        rootView.findViewById<ComposeView>(R.id.compose_view).apply {
            setContent {
                    PreferencesRoot(
                        viewModel = viewModel,
                        onBackPressed = {
                            activity?.onBackPressedDispatcher?.onBackPressed()
                        }
                    )

            }
        }
        return rootView
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
