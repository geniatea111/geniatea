package com.example.compose.geniatea.presentation.funcionalidades.formalizer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.compose.geniatea.R
import com.example.compose.geniatea.theme.GenIATEATheme


class FormalizerFragment : Fragment() {
    private val viewModel: FormalizerViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.fragment_profile, container, false)

        rootView.findViewById<ComposeView>(R.id.toolbar_compose_view).apply {

            viewModel.navigationEvent.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { action ->
                    when (action) {
                        is FormalizerAction.OnBackPressed -> activity?.onBackPressedDispatcher?.onBackPressed()
                        is FormalizerAction.OnCopyPressed -> {
                            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Copied Text", action.text)
                            clipboard.setPrimaryClip(clip)
                            Log.i("ChatFragment", "Message copied to clipboard: ${action.text}")
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
                GenIATEATheme {
                    FormalizerRoot(
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
