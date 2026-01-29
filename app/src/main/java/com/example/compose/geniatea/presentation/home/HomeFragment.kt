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
import com.example.compose.geniatea.theme.GenIATEATheme
import androidx.fragment.app.activityViewModels
import com.example.compose.geniatea.presentation.settingsSection.settings.SettingsViewModel
import com.example.compose.geniatea.presentation.settingsSection.appColor.AppColorViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue


class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by activityViewModels()
    private val appColorViewModel: AppColorViewModel by activityViewModels()

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
                HomeRoot(
                    viewModel = viewModel,
                    onBackPressed = {
                        activity?.onBackPressedDispatcher?.onBackPressed()
                    }
                )
            }
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
                            putString("topic", action.topic)
                        }
                        findNavController().navigate(R.id.action_nav_home_to_nav_chat, bundle)
                    }
                }
            }
        }
    }

}
