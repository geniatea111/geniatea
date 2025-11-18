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
import kotlinx.coroutines.launch
import kotlin.getValue

class AccountFragment : Fragment() {
    private val viewModel: AccountViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.fragment_profile, container, false)

        rootView.findViewById<ComposeView>(R.id.toolbar_compose_view).apply {

            val storeDataUser = StoreDataUser()

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
                                storeDataUser.refreshTokens(requireContext(), action.accessToken, action.refreshToken)
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
                    AccountRoot(
                        viewModel = viewModel,
                        onBackPressed = {
                            activity?.onBackPressedDispatcher?.onBackPressed()
                        }
                    )
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