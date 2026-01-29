package com.example.compose.geniatea.presentation.userManagementSection.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.compose.geniatea.R
import com.example.compose.geniatea.data.StoreDataUser
import com.example.compose.geniatea.theme.GenIATEATheme
import com.example.compose.geniatea.presentation.userManagementSection.login.LoginAction
import com.example.compose.geniatea.presentation.settingsSection.settings.SettingsViewModel
import com.example.compose.geniatea.presentation.settingsSection.appColor.AppColorViewModel
import androidx.fragment.app.activityViewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.launch


class RegisterFragment : Fragment() {
    private val viewModel: RegisterViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by activityViewModels()
    private val appColorViewModel: AppColorViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.fragment_profile, container, false)
        val storeDataUser = StoreDataUser(requireContext())

        rootView.findViewById<ComposeView>(R.id.toolbar_compose_view).apply {

            viewModel.navigationEvent.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { action ->
                    when (action) {
                        is RegisterAction.OnBackPressed -> activity?.onBackPressedDispatcher?.onBackPressed()
                        is RegisterAction.OnRegisterClicked -> viewModel.register(requireContext())
                        is RegisterAction.OnRegisterSuccess -> {
                            viewLifecycleOwner.lifecycleScope.launch {
                                storeDataUser.saveUser(action.user)
                                findNavController().navigate(R.id.action_nav_register_to_nav_onboarding)
                            }
                        }
                        is RegisterAction.OnRegisterError -> {
                            Toast.makeText(requireContext(), action.error, Toast.LENGTH_SHORT).show()
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
                    RegisterRoot(
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