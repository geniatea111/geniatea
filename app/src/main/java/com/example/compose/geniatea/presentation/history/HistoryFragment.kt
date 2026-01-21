package com.example.compose.geniatea.presentation.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.compose.geniatea.R
import com.example.compose.geniatea.data.StoreDataUser
import com.example.compose.geniatea.theme.GenIATEATheme

class HistoryFragment : Fragment() {

    private val viewModel: HistoryViewModel by viewModels {
        HistoryViewModelFactory(StoreDataUser(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                GenIATEATheme {
                    HistoryScreen(
                        viewModel = viewModel,
                        onAction = { action: HistoryAction ->
                            when (action) {
                                HistoryAction.OnBackPressed -> findNavController().popBackStack()
                                is HistoryAction.OnSessionClicked -> {
                                    val bundle = bundleOf("sessionId" to action.sessionId)
                                    findNavController().navigate(R.id.nav_chat, bundle)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

class HistoryViewModelFactory(private val dataStore: StoreDataUser) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryViewModel(dataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
