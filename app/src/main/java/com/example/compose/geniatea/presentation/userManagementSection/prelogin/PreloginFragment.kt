package com.example.compose.geniatea.presentation.userManagementSection.prelogin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.compose.geniatea.R
import com.example.compose.geniatea.theme.GenIATEATheme


class PreloginFragment : Fragment() {
    private val viewModel: PreloginViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.fragment_profile, container, false)

        rootView.findViewById<ComposeView>(R.id.toolbar_compose_view).apply {

            viewModel.navigationEvent.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { action ->
                    when (action) {
                        is PreloginAction.OnBackPressed -> activity?.onBackPressedDispatcher?.onBackPressed()
                        is PreloginAction.OnLoginPressed -> findNavController().navigate(R.id.nav_login)
                        is PreloginAction.OnRegisterPressed -> //findNavController().navigate(R.id.nav_register)
                            findNavController().navigate(R.id.nav_preregister)
                        is PreloginAction.OnTermsOfServiceClicked -> {
                           /* val url = "https://geniatea.com/terms"
                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
                            intent.data = android.net.Uri.parse(url)
                            startActivity(intent)*/
                           // findNavController().navigate(R.id.nav_terms)

                        }
                        is PreloginAction.OnPrivacyPolicyClicked -> {
                            /*val url = "https://geniatea.com/privacy"
                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
                            intent.data = android.net.Uri.parse(url)
                            startActivity(intent)*/
                            findNavController().navigate(R.id.nav_privacy)
                        }

                    }
                }
            }


        }

        rootView.findViewById<ComposeView>(R.id.compose_view).apply {
            setContent {
                GenIATEATheme {
                    PreloginRoot(
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