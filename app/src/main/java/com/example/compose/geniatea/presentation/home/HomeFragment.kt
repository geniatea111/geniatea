package com.example.compose.geniatea.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.material3.Snackbar
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.compose.geniatea.R
import com.example.compose.geniatea.theme.GenIATEATheme
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import kotlin.getValue

class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.fragment_profile, container, false)

        rootView.findViewById<ComposeView>(R.id.toolbar_compose_view).apply {

            viewModel.navigationEvent.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { action ->
                    when (action) {
                        is HomeAction.OnBackPressed -> activity?.onBackPressedDispatcher?.onBackPressed()
                        is HomeAction.OnAccountPressed -> findNavController().navigate(R.id.nav_settings)
                        is HomeAction.OnChatPressed -> findNavController().navigate(R.id.nav_chat)
                        is HomeAction.OnJudgePressed -> findNavController().navigate(R.id.nav_judge)
                        is HomeAction.OnFormalizerPressed -> findNavController().navigate(R.id.nav_formalizer)
                        is HomeAction.OnResourcesPressed -> findNavController().navigate(R.id.nav_resources)
                        is HomeAction.OnTaskListPressed -> findNavController().navigate(R.id.nav_tasklist)
                        else -> {
                            // Handle other actions if needed
                        }
                    }
                }
            }


        }


        rootView.findViewById<ComposeView>(R.id.compose_view).apply {
            setContent {
                GenIATEATheme {
                    HomeRoot(
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