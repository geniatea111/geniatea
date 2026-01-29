package com.example.compose.geniatea.presentation.settingsSection.changePass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.compose.geniatea.R
import com.example.compose.geniatea.theme.GenIATEATheme
import kotlinx.coroutines.launch
import com.example.compose.geniatea.presentation.settingsSection.settings.SettingsViewModel
import com.example.compose.geniatea.presentation.settingsSection.appColor.AppColorViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

class ChangePassFragment : Fragment() {
    private val viewModel: ChangePassViewModel by activityViewModels()
    private val settingsViewModel: SettingsViewModel by activityViewModels()
    private val appColorViewModel: AppColorViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.fragment_profile, container, false)

        rootView.findViewById<ComposeView>(R.id.toolbar_compose_view).apply {

            viewModel.actionEvent.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { action ->
                    when (action) {
                        is ChangePassAction.OnBackPressed -> activity?.onBackPressedDispatcher?.onBackPressed()
                        is ChangePassAction.OnChangePassClicked -> {
                            try{
                                lifecycleScope.launch {
                                    viewModel.changePassword(requireContext())
                                }
                            }catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        is ChangePassAction.OnChangePassSuccess -> {
                            Toast.makeText(
                                requireContext(),
                                "Contraseña cambiada correctamente",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is ChangePassAction.OnChangePassError -> {
                            Toast.makeText(
                                requireContext(),
                                "Error al cambiar la contraseña",
                                Toast.LENGTH_SHORT
                            ).show()
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
                    ChangePassRoot(
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