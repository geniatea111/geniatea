package com.example.compose.geniatea.presentation.funcionalidades.chat

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.compose.geniatea.R
import com.example.compose.geniatea.theme.GenIATEATheme
import androidx.fragment.app.activityViewModels
import com.example.compose.geniatea.presentation.settingsSection.settings.SettingsViewModel
import com.example.compose.geniatea.presentation.settingsSection.appColor.AppColorViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.launch
import java.util.Locale


class ChatFragment : Fragment() {
    private val viewModel: ChatViewModel by viewModels()
    var tts: TextToSpeech? = null

    private val requestPermissionLauncher = registerForActivityResult( // cambiar esto para que se pida cuando se hace click en el boton
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(requireContext(), "Permiso de micrófono denegado", Toast.LENGTH_SHORT).show()
        }
    }

    private val imagePicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        Log.d("ChatFragment", "Image picker result: $uri")
        uri?.let {
            viewModel.onAction(ChatAction.OnImagePicked(it))
        }
    }

    private val settingsViewModel: SettingsViewModel by activityViewModels()
    private val appColorViewModel: AppColorViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.fragment_profile, container, false)

        val sessionId = arguments?.getLong("sessionId", -1L) ?: -1L
        if (sessionId != -1L) {
            viewModel.setSessionId(sessionId)
            val topic = arguments?.getString("topic") ?: ""
            if (topic.isNotEmpty()) {
                viewModel.setTopic(topic)
            }
            viewModel.loadChatSession(requireContext())
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
        
        viewModel.loadAvatar(requireContext())

        rootView.findViewById<ComposeView>(R.id.toolbar_compose_view).apply {

            viewModel.navigationEvent.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { action ->
                    when (action) {
                        is ChatAction.OnBackPressed -> activity?.onBackPressedDispatcher?.onBackPressed()
                        is ChatAction.OnAccountPressed -> findNavController().navigate(R.id.nav_settings)
                        is ChatAction.OnImageSelection -> {
                            Log.i("ChatFragment", "Image selection action triggered")
                            imagePicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                            )
                        }

                        is ChatAction.OnSoundPressed -> {
                            //text to speech
                            tts = TextToSpeech(requireContext()) { status ->
                                if (status == TextToSpeech.SUCCESS) {
                                    val result = tts?.setLanguage(Locale.getDefault())
                                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                        Log.e("ChatFragment", "Language not supported")
                                    } else {
                                        tts?.speak(action.message, TextToSpeech.QUEUE_FLUSH, null, null)
                                        Log.i("ChatFragment", "Speaking message: ${action.message}")
                                    }
                                } else {
                                    Log.e("ChatFragment", "Initialization failed")
                                }
                            }
                        }

                        is ChatAction.OnCopyPressed -> {
                            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("message", action.message)
                            clipboard.setPrimaryClip(clip)

                            Log.i("ChatFragment", "Message copied to clipboard: ${action.message}")

                        }

                        is ChatAction.OnStartRecording -> {
                            startRecording()
                        }

                        is ChatAction.OnStopRecording -> {
                            stopRecording()
                        }

                        is ChatAction.OnMessageSend -> {
                            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            val view = requireActivity().currentFocus ?: View(requireContext())
                            imm.hideSoftInputFromWindow(view.windowToken, 0)

                            try{
                                lifecycleScope.launch {
                                    viewModel.sendMessage(requireContext(), action.message)
                                }
                            }catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        ChatAction.OnOptionsPressed -> {
                            //show a bottom sheet with options
                            //bottom sheet implementation


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
                    ChatRoot(
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

    override fun onDestroyView() {
        super.onDestroyView()
        tts?.stop()
        tts?.shutdown()
        tts = null
    }


    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private fun startRecording() { // mirar si se puede cambiar esto con vosk

        val speechRecognizer: SpeechRecognizer? = SpeechRecognizer.createSpeechRecognizer(requireContext())

        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        speechRecognizer?.setRecognitionListener(
            object : RecognitionListener {
                override fun onReadyForSpeech(bundle: Bundle) {}

                override fun onBeginningOfSpeech() {}

                override fun onRmsChanged(v: Float) {}

                override fun onBufferReceived(bytes: ByteArray) {}

                override fun onEndOfSpeech() {}

                override fun onError(i: Int) {}

                override fun onResults(bundle: Bundle) {
                    val matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (matches != null)
                        if (matches.isNotEmpty()) {
                            val recognizedText = matches[0]
                            viewModel.onAction(ChatAction.OnMessageChange(TextFieldValue(recognizedText)))

                            Log.i("ChatFragment", "Speech recognition result: ${viewModel.state.value.currentMessage.text}")

                        } else {
                            Log.w("ChatFragment", "No speech recognized")
                        }
                    else {
                        Log.w("ChatFragment", "No matches found in results")
                    }
                }

                override fun onPartialResults(bundle: Bundle) {}

                override fun onEvent(i: Int, bundle: Bundle) {}
            }
        )

        speechRecognizer?.startListening(speechRecognizerIntent)
        Log.i("ChatFragment", "Speech recognition started")
    }

    private fun stopRecording() {
        val speechRecognizer: SpeechRecognizer? = SpeechRecognizer.createSpeechRecognizer(requireContext())

        if (speechRecognizer != null) {
            speechRecognizer.stopListening()
            speechRecognizer.destroy()
            Log.i("ChatFragment", "Speech recognition stopped")
        } else {
            Log.w("ChatFragment", "Speech recognizer is not initialized")
        }

    }

}
