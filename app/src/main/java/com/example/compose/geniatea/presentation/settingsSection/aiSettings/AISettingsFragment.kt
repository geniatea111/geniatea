package com.example.compose.geniatea.presentation.settingsSection.aiSettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.compose.geniatea.R
import com.example.compose.geniatea.theme.GenIATEATheme
import android.widget.Toast
import kotlin.getValue

import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest

class AISettingsFragment : Fragment() {
    private val viewModel: AISettingsViewModel by activityViewModels()

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            viewModel.onAvatarSelected(uri, requireContext())
        } else {
            // User cancelled the picker
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.fragment_profile, container, false)

        rootView.findViewById<ComposeView>(R.id.toolbar_compose_view).apply {

            viewModel.actionEvent.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { action ->
                    when (action) {
                        is AISettingsAction.OnBackPressed -> activity?.onBackPressedDispatcher?.onBackPressed()
                        is AISettingsAction.ShowToast -> Toast.makeText(requireContext(), action.message, Toast.LENGTH_SHORT).show()
                        AISettingsAction.OpenGallery -> {
                            pickMedia.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        else -> Unit
                    }
                }
            }

        }

        rootView.findViewById<ComposeView>(R.id.compose_view).apply {
            setContent {
                    AISettingsRoot(
                        viewModel = viewModel,
                        onBackPressed = {
                            activity?.onBackPressedDispatcher?.onBackPressed()
                        }
                    )
                }

        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadSettings(requireContext())
    }


}