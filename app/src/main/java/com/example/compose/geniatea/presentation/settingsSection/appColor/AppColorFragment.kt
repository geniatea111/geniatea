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

class AppColorFragment : Fragment() {
    private val viewModel: AppColorViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.fragment_profile, container, false)

        rootView.findViewById<ComposeView>(R.id.toolbar_compose_view).apply {

            viewModel.actionEvent.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { action ->
                    when(action){
                        AppColorAction.OnBackPressed -> activity?.onBackPressedDispatcher?.onBackPressed()
                        AppColorAction.OnBluePressed -> viewModel.updateThemeVariant(AppColorVariant.BLUE, requireContext())
                        AppColorAction.OnPinkPressed -> viewModel.updateThemeVariant(AppColorVariant.PINK, requireContext())
                    }
                }
            }


        }

        rootView.findViewById<ComposeView>(R.id.compose_view).apply {
            setContent {
                val theme by viewModel.themeVariant.collectAsState()
                GenIATEATheme(themeVariant = theme) {
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