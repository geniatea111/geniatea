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
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import android.widget.Toast


class PreRegisterFragment : Fragment() {
    private val viewModel: PreRegisterViewModel by viewModels()
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
                    PreRegisterRoot(
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