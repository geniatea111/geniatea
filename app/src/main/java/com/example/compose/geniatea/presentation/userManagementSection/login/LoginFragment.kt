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

class LoginFragment : Fragment() {
    private val viewModel: LoginViewModel by viewModels()

    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.fragment_profile, container, false)
        val token = getString(R.string.google_client_id)

        val storeDataUser = StoreDataUser()

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
                                storeDataUser.saveUser(requireContext(), action.user)
                                findNavController().navigate(R.id.nav_home)
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
                    LoginRoot(
                        viewModel = viewModel,
                        onBackPressed = {
                            activity?.onBackPressedDispatcher?.onBackPressed()
                        }
                    )

            }
        }
        return rootView
    }

}