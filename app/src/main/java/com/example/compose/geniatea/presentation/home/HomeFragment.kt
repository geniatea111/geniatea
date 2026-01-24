package com.example.compose.geniatea.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.compose.geniatea.R
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val composeView = view.findViewById<ComposeView>(R.id.compose_view)
        composeView.setContent {
            HomeRoot(
                viewModel = viewModel,
                onBackPressed = {
                    activity?.onBackPressedDispatcher?.onBackPressed()
                }
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.setData(requireContext())
        }

        viewModel.navigationEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { action ->
                when (action) {
                    is HomeAction.OnBackPressed -> activity?.onBackPressedDispatcher?.onBackPressed()
                    is HomeAction.OnAccountPressed -> findNavController().navigate(R.id.nav_settings)
                    is HomeAction.OnChatPressed -> findNavController().navigate(R.id.action_nav_home_to_nav_chat)
                    is HomeAction.OnJudgePressed -> findNavController().navigate(R.id.nav_judge)
                    is HomeAction.OnFormalizerPressed -> findNavController().navigate(R.id.nav_formalizer)
                    is HomeAction.OnResourcesPressed -> findNavController().navigate(R.id.nav_resources)
                    is HomeAction.OnTaskListPressed -> findNavController().navigate(R.id.nav_tasklist)
                    is HomeAction.OnSeeAllRecentChatsPressed -> findNavController().navigate(R.id.nav_history)
                    is HomeAction.OnRecentChatPressed -> {
                        val bundle = Bundle().apply {
                            putLong("sessionId", action.conversationId)
                        }
                        findNavController().navigate(R.id.action_nav_home_to_nav_chat, bundle)
                    }
                }
            }
        }
    }

}
