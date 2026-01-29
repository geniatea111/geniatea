package com.example.compose.geniatea.presentation.settingsSection.accountSettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.compose.geniatea.R
import com.example.compose.geniatea.data.StoreDataUser
import com.example.compose.geniatea.theme.GenIATEATheme
import com.example.compose.geniatea.presentation.settingsSection.settings.SettingsViewModel
import com.example.compose.geniatea.presentation.settingsSection.appColor.AppColorViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.launch

class AccountFragment : Fragment() {
    private val viewModel: AccountViewModel by activityViewModels()
    private val settingsViewModel: SettingsViewModel by activityViewModels()
    private val appColorViewModel: AppColorViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.fragment_profile, container, false)

        rootView.findViewById<ComposeView>(R.id.toolbar_compose_view).apply {

            val storeDataUser = StoreDataUser(context)

            viewModel.actionEvent.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { action ->
                    when(action) {
                        is AccountAction.OnBackPressed -> {
                            activity?.onBackPressedDispatcher?.onBackPressed()
                        }
                        is AccountAction.OnPasswordUpdatePressed -> {
                            findNavController().navigate(R.id.nav_changepass)
                        }
                        is AccountAction.OnUpdatePressed -> {
                            try{
                                lifecycleScope.launch {
                                    viewModel.updateData(requireContext())
                                }
                            }catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        is AccountAction.OnUpdateSuccess -> {
                            Toast.makeText(requireContext(), "Información actualizada correctamente", Toast.LENGTH_SHORT).show()
                        }
                        is AccountAction.OnUpdateError -> {
                            Toast.makeText(requireContext(), "Error al actualizar la información. Prueba más tarde", Toast.LENGTH_SHORT).show()
                        }
                        is AccountAction.OnDeleteAccountPressed -> {
                            lifecycleScope.launch {
                                viewModel.deleteAccount(requireContext())
                            }
                        }
                        is AccountAction.OnDeleteAccountSuccess -> {
                            Toast.makeText(requireContext(), "Cuenta eliminada correctamente", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.nav_prelogin)
                        }
                        is AccountAction.OnDeleteAccountError -> {
                            Toast.makeText(requireContext(), "Error al eliminar la cuenta. Prueba más tarde", Toast.LENGTH_SHORT).show()
                        }
                        is AccountAction.OnRefreshTokenSuccess -> {
                            viewLifecycleOwner.lifecycleScope.launch {
                                storeDataUser.refreshTokens(action.accessToken, action.refreshToken)
                            }
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
                    AccountRoot(
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.setData(requireContext())
        }
    }

}