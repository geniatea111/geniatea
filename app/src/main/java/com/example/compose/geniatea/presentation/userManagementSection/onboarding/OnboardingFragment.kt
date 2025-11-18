package com.example.compose.geniatea.presentation.userManagementSection.onboarding

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.compose.geniatea.R
import com.example.compose.geniatea.theme.GenIATEATheme


class OnboardingFragment : Fragment() {
    private val viewModel: OnboardingViewModel by viewModels()

    private val imagePicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            viewModel.updateImage(it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.fragment_profile, container, false)

        rootView.findViewById<ComposeView>(R.id.toolbar_compose_view).apply {

            viewModel.navigationEvent.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { action ->
                    when (action) {
                        is OnboardingAction.OnBackPressed -> activity?.onBackPressedDispatcher?.onBackPressed()
                        is OnboardingAction.OnContinuePressed -> findNavController().navigate(R.id.nav_home)
                        is OnboardingAction.OnSkipPressed -> findNavController().navigate(R.id.nav_home)
                        is OnboardingAction.OnGenerateImagePressed -> {
                            imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                        else -> Unit

                    }
                }
            }
        }

        rootView.findViewById<ComposeView>(R.id.compose_view).apply {
            setContent {
                GenIATEATheme {
                    OnboardingRoot(
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