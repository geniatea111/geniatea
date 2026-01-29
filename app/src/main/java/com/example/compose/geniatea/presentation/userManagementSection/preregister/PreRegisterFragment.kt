package com.example.compose.geniatea.presentation.userManagementSection.preregister

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.compose.geniatea.R
import com.example.compose.geniatea.data.StoreDataUser
import com.example.compose.geniatea.theme.GenIATEATheme
import kotlinx.coroutines.launch
import com.example.compose.geniatea.presentation.settingsSection.settings.SettingsViewModel
import com.example.compose.geniatea.presentation.settingsSection.appColor.AppColorViewModel
import androidx.fragment.app.activityViewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import android.widget.Toast


class PreRegisterFragment : Fragment() {
    private val viewModel: PreRegisterViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by activityViewModels()
    private val appColorViewModel: AppColorViewModel by activityViewModels()
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.fragment_profile, container, false)
        val token = getString(R.string.google_client_id)
        val storeDataUser = StoreDataUser(requireContext())

        googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            lifecycleScope.launch {
                viewModel.handleGoogleSignInResult(result.data, requireContext())
            }
        }

        viewModel.launchGoogleSignIn.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(token)
                    .requestEmail()
                    .build()

                val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
                googleSignInLauncher.launch(googleSignInClient.signInIntent)
            }
        }

        rootView.findViewById<ComposeView>(R.id.toolbar_compose_view).apply {

            viewModel.navigationEvent.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { action ->
                    when (action) {
                        is PreRegisterAction.OnBackPressed -> activity?.onBackPressedDispatcher?.onBackPressed()
                        is PreRegisterAction.OnContinueClicked -> viewModel.continueClicked(requireContext())
                        is PreRegisterAction.OnEmailSuccess -> {
                            viewLifecycleOwner.lifecycleScope.launch {
                                findNavController().navigate(R.id.nav_register)
                            }
                        }
                        is PreRegisterAction.OnGoogleLoginSuccess -> {
                            viewLifecycleOwner.lifecycleScope.launch {
                                storeDataUser.saveUser(action.user)
                                findNavController().navigate(R.id.nav_home)
                            }
                        }
                        is PreRegisterAction.OnGoogleLoginError -> {
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
                    PreRegisterRoot(
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