package com.example.compose.geniatea.presentation.settingsSection.settings

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.compose.geniatea.R
import com.example.compose.geniatea.theme.GenIATEATheme
import kotlinx.coroutines.launch
import kotlin.getValue


class SettingsFragment : Fragment() {
    private val viewModel: SettingsViewModel by activityViewModels()

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
                                viewModel.logoutUser(requireContext())
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
                        is SettingsAction.OnPreferencesPressed -> {
                            findNavController().navigate(R.id.nav_preferences)
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
                GenIATEATheme {
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
}