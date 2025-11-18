package com.example.compose.geniatea.presentation.funcionalidades.resourcestea

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.compose.geniatea.R
import com.example.compose.geniatea.theme.GenIATEATheme


class ResourcesFragment : Fragment() {
    private val viewModel: ResourcesViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.fragment_profile, container, false)

        rootView.findViewById<ComposeView>(R.id.toolbar_compose_view).apply {

            viewModel.navigationEvent.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { action ->
                    when (action) {
                        is ResourcesAction.OnBackPressed -> activity?.onBackPressedDispatcher?.onBackPressed()

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
                    ResourcesRoot(
                        viewModel = viewModel,
                        onBackPressed = {
                            activity?.onBackPressedDispatcher?.onBackPressed()
                        },
                    )
                }
            }
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}
