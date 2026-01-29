package com.example.compose.geniatea.presentation.settingsSection.appColor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.compose.geniatea.R
import com.example.compose.geniatea.theme.AppColorVariant
import com.example.compose.geniatea.theme.GenIATEATheme
import androidx.compose.runtime.getValue

import kotlin.getValue
import com.example.compose.geniatea.presentation.settingsSection.settings.SettingsViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

class AppColorFragment : Fragment() {
    private val viewModel: AppColorViewModel by activityViewModels()
    private val settingsViewModel: SettingsViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.fragment_profile, container, false)

        rootView.findViewById<ComposeView>(R.id.toolbar_compose_view).apply {

            viewModel.actionEvent.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { action ->
                    when(action){
                        AppColorAction.OnBackPressed -> activity?.onBackPressedDispatcher?.onBackPressed()
                        AppColorAction.OnBluePressed -> viewModel.updateThemeVariant(AppColorVariant.BLUE)
                        AppColorAction.OnPinkPressed -> viewModel.updateThemeVariant(AppColorVariant.PINK)
                    }
                }
            }


        }

        rootView.findViewById<ComposeView>(R.id.compose_view).apply {
            setContent {
                val theme by viewModel.themeVariant.collectAsState()
                val fontSize by settingsViewModel.fontSize.collectAsState()
                val isDark by settingsViewModel.darkMode.collectAsState()

                val fontScale = when(fontSize) {
                    "S" -> 0.85f
                    "L" -> 1.15f
                    else -> 1.0f
                }

                GenIATEATheme(
                    themeVariant = theme,
                    isDarkTheme = isDark,
                    fontScale = fontScale
                ) {
                    AppIconRoot(
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

}