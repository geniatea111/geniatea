package com.example.compose.geniatea.presentation.funcionalidades.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.compose.geniatea.theme.GenIATEATheme
import androidx.fragment.app.activityViewModels
import com.example.compose.geniatea.presentation.settingsSection.settings.SettingsViewModel
import com.example.compose.geniatea.presentation.settingsSection.appColor.AppColorViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

class FavoriteResponsesFragment : Fragment() {
    private val viewModel: FavoriteResponsesViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by activityViewModels()
    private val appColorViewModel: AppColorViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
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
                    FavoriteResponsesRoot(
                        viewModel = viewModel,
                        onBackPressed = {
                            findNavController().popBackStack()
                        },
                        onNavigateToChat = { sessionId ->
                             val bundle = Bundle().apply {
                                 putLong("sessionId", sessionId)
                             }
                             findNavController().navigate(com.example.compose.geniatea.R.id.action_nav_favorites_to_nav_chat, bundle)
                        }
                    )
                }
            }
        }
    }
}
