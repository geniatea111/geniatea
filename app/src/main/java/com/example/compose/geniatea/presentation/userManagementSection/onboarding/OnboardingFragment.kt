package com.example.compose.geniatea.presentation.userManagementSection.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.compose.geniatea.R
import com.example.compose.geniatea.utils.Event
import com.example.compose.geniatea.theme.GenIATEATheme
import com.example.compose.geniatea.presentation.settingsSection.settings.SettingsViewModel
import com.example.compose.geniatea.presentation.settingsSection.appColor.AppColorViewModel
import androidx.fragment.app.activityViewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

class OnboardingFragment : Fragment() {

    private val viewModel: OnboardingViewModel by viewModels { 
        OnboardingViewModelFactory(requireContext())
    }
    private val settingsViewModel: SettingsViewModel by activityViewModels()
    private val appColorViewModel: AppColorViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
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
                    OnboardingScreen(viewModel = viewModel) {
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.navigationEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { navigationEvent ->
                when (navigationEvent) {
                    is OnboardingNavigationEvent.OnRegisterSuccess -> {
                        findNavController().navigate(R.id.action_nav_onboarding_to_nav_home)
                    }
                    is OnboardingNavigationEvent.OnRegisterError -> {
                        // You can show a toast or a snackbar here
                    }
                }
            }
        }
    }
}