package com.example.compose.geniatea.presentation.userManagementSection.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.compose.geniatea.R
import com.example.compose.geniatea.data.StoreDataUser
import com.example.compose.geniatea.theme.GenIATEATheme
import com.example.compose.geniatea.presentation.userManagementSection.login.LoginAction
import kotlinx.coroutines.launch


class RegisterFragment : Fragment() {
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.fragment_profile, container, false)
        val storeDataUser = StoreDataUser()

        rootView.findViewById<ComposeView>(R.id.toolbar_compose_view).apply {

            viewModel.navigationEvent.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { action ->
                    when (action) {
                        is RegisterAction.OnBackPressed -> activity?.onBackPressedDispatcher?.onBackPressed()
                        is RegisterAction.OnRegisterClicked -> viewModel.register(requireContext())
                        is RegisterAction.OnRegisterSuccess -> {
                            viewLifecycleOwner.lifecycleScope.launch {
                                storeDataUser.saveUser(requireContext(), action.user)
                                findNavController().navigate(R.id.nav_home)
                            }
                        }
                        is RegisterAction.OnRegisterError -> {
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
                    RegisterRoot(
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