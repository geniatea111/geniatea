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
import androidx.compose.ui.text.TextRange
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
        viewModel.loadContinuousVoicePreference(requireContext())

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
                                        tts?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                                            override fun onStart(utteranceId: String?) {
                                                viewModel.setSpeaking(true)
                                            }

                                            override fun onDone(utteranceId: String?) {
                                                viewModel.setSpeaking(false)
                                            }

                                            override fun onError(utteranceId: String?) {
                                                viewModel.setSpeaking(false)
                                            }
                                        })
                                        
                                        val params = Bundle()
                                        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "messageID")
                                        tts?.speak(action.message, TextToSpeech.QUEUE_FLUSH, params, "messageID")
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


    private var speechRecognizer: SpeechRecognizer? = null
    private var isContinuousListening = false

    override fun onResume() {
        super.onResume()
        // Check if we should start listening automatically
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                if (state.isContinuousVoiceEnabled && !isContinuousListening) {
                     startContinuousListening()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopRecording()
    }


    private fun startRecording() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            startListeningInternal(continuous = false)
        } else {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun startContinuousListening() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            startListeningInternal(continuous = true)
        }
    }

    private fun startListeningInternal(continuous: Boolean) {
        if (speechRecognizer != null) {
            speechRecognizer?.destroy()
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        isContinuousListening = continuous

        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        // Avoid partial results to reduce noise in continuous mode if not needed
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)

        speechRecognizer?.setRecognitionListener(
            object : RecognitionListener {
                override fun onReadyForSpeech(bundle: Bundle) {}

                override fun onBeginningOfSpeech() {}

                override fun onRmsChanged(v: Float) {}

                override fun onBufferReceived(bytes: ByteArray) {}

                override fun onEndOfSpeech() {
                    Log.i("ChatFragment", "End of speech")
                }

                override fun onError(i: Int) {
                    Log.e("ChatFragment", "Speech error: $i")
                     // Restart if continuous
                    if (isContinuousListening) {
                         // Add a small delay to avoid rapid looping on error
                        view?.postDelayed({
                            if (isContinuousListening && isResumed) {
                                startContinuousListening()
                            }
                        }, 1000)
                    }
                }

                override fun onResults(bundle: Bundle) {
                    val matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (matches != null && matches.isNotEmpty()) {
                        val recognizedText = matches[0]
                        Log.i("ChatFragment", "Recognized: $recognizedText")

                        if (isContinuousListening) {
                            // Keyword detection
                            val keyword = viewModel.state.value.continuousVoiceKeyword
                            if (recognizedText.contains(keyword, ignoreCase = true)) {
                                
                                // Remove the keyword and trim
                                var query = recognizedText.replace(keyword, "", ignoreCase = true).trim()
                                
                                // Clean up potential prefixes (I, i, |) using Regex for robustness
                                // Fix for user reported issue: aggressive prefix cleaning
                                query = query.replace(Regex("^[|iI]\\\\s*"), "")

                                if (query.isNotEmpty() && !query.equals("I", ignoreCase = true) && !query.equals("|", ignoreCase = true)) {
                                    val currentText = viewModel.state.value.currentMessage.text
                                    val newText = if (currentText.isEmpty()) query else "$currentText $query"

                                    viewModel.onAction(ChatAction.OnMessageChange(TextFieldValue(newText, selection = TextRange(newText.length))))
                                    // Auto-send removed per user request
                                }
                            }
                            
                            // Restart listening
                            startContinuousListening()
                            
                        } else {
                            // Normal mode
                            viewModel.onAction(ChatAction.OnMessageChange(TextFieldValue(recognizedText)))
                        }

                    } else {
                        Log.w("ChatFragment", "No matches found")
                         if (isContinuousListening) {
                            startContinuousListening()
                        }
                    }
                }

                override fun onPartialResults(bundle: Bundle) {}

                override fun onEvent(i: Int, bundle: Bundle) {}
            }
        )

        speechRecognizer?.startListening(speechRecognizerIntent)
        Log.i("ChatFragment", "Speech recognition started (Continuous: $continuous)")
    }

    private fun stopRecording() {
        isContinuousListening = false
        if (speechRecognizer != null) {
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
            speechRecognizer = null
            Log.i("ChatFragment", "Speech recognition stopped")
        }
    }

}
