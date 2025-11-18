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


class PreRegisterFragment : Fragment() {
    private val viewModel: PreRegisterViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.fragment_profile, container, false)

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