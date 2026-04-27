package com.example.compose.geniatea.presentation.userManagementSection.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.navigation.findNavController
import com.example.compose.geniatea.R
import com.example.compose.geniatea.data.StoreDataUser
import com.example.compose.geniatea.theme.GenIATEATheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.launch
import com.example.compose.geniatea.notifications.FcmTokenManager
import com.example.compose.geniatea.presentation.settingsSection.settings.SettingsViewModel
import com.example.compose.geniatea.presentation.settingsSection.appColor.AppColorViewModel
import androidx.fragment.app.activityViewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

class LoginFragment : Fragment() {
    private val viewModel: LoginViewModel by viewModels()
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
                  //  .requestScopes(Scope("https://www.googleapis.com/auth/user.birthday.read"))
                   // .requestScopes(Scope("https://www.googleapis.com/auth/user.gender.read"))
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
                        is LoginAction.OnLoginClicked -> viewModel.login(requireContext())
                        is LoginAction.OnBackPressed -> activity?.onBackPressedDispatcher?.onBackPressed()
                        is LoginAction.OnRegisterClicked -> findNavController().navigate(R.id.nav_preregister)
                        is LoginAction.OnLoginSuccess -> {
                            viewLifecycleOwner.lifecycleScope.launch {
                                storeDataUser.saveUser(action.user)
                                // Token ya está en DataStore: el interceptor lo incluirá en esta petición
                                FcmTokenManager.refreshAndSendToken(requireContext())
                                if (action.user.onboardingCompleted) {
                                    findNavController().navigate(R.id.nav_home)
                                } else {
                                    findNavController().navigate(R.id.nav_onboarding)
                                }
                            }
                        }
                        is LoginAction.OnLoginError -> {
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
                    LoginRoot(
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