package com.example.frontend.presentation.view

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.util.Pools
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.frontend.R
import com.example.frontend.databinding.FragmentFargCameraBinding
import com.example.frontend.databinding.FragmentFragChatBinding
import com.example.frontend.presentation.viewModel.HomeViewModel
import com.example.frontend.presentation.viewModel.observeOnce
import com.google.mlkit.nl.languageid.LanguageIdentification
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale
import java.util.jar.Manifest

@AndroidEntryPoint
class fragChat : Fragment(), TextToSpeech.OnInitListener {

    private val viewmodel: HomeViewModel by viewModels()


    private lateinit var tts: TextToSpeech
    private lateinit var languagePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var languagePickerLauncher2: ActivityResultLauncher<Intent>

    private val requestMicPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startSpeechToText()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Microphone permission is required",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private val speechRecognizerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val matches = result.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (!matches.isNullOrEmpty()) {
                    if (side == SIDE.Speaker) {
                        binding.textViewSpeakerSide.text = matches[0]
                        viewmodel.translateText(
                            matches[0],
                            binding.ListenerLanguage.text.toString()
                        )
                        viewmodel.translateText(matches[0], binding.ListenerLanguage.text.toString()) { translated ->
                            binding.textViewListenerSide.text = translated
                        }
                    } else if(side == SIDE.Listener){
                        binding.textViewListenerSide.text = matches[0]
                        viewmodel.translateText(
                            matches[0],
                            binding.languageSpeakerSide.text.toString()
                        )
                        viewmodel.translateText(matches[0], binding.languageSpeakerSide.text.toString()) { translated ->
                            binding.textViewSpeakerSide.text = translated
                        }
                    }

                }
            }
        }

    private var _binding: FragmentFragChatBinding? = null
    private val binding get() = _binding!!
    private var side: String = SIDE.Speaker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFragChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tts = TextToSpeech(requireContext(), this)

        view.findViewById<ImageView>(R.id.SpeakerSpeaker).setColorFilter(
            ContextCompat.getColor(requireContext(), R.color.appBlue),
            PorterDuff.Mode.SRC_IN
        )
        view.findViewById<ImageView>(R.id.ListenerSpeaker).setColorFilter(
            ContextCompat.getColor(requireContext(), R.color.appBlue),
            PorterDuff.Mode.SRC_IN
        )

        languagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedLanguage = result.data?.getStringExtra("selected_language_for_camera")
                selectedLanguage?.let {
                    binding.ListenerLanguage.text = it

                }
            }
        }
        languagePickerLauncher2 = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedLanguage = result.data?.getStringExtra("selected_language_for_camera")
                selectedLanguage?.let {
                    binding.languageSpeakerSide.text = it

                }
            }
        }


        binding.ListenerLanguage.setOnClickListener {
            val intent = Intent(requireContext(), SelectLanguage::class.java)
            intent.putExtra("from", "camera")
            languagePickerLauncher.launch(intent)
        }
        binding.languageSpeakerSide.setOnClickListener {
            val intent = Intent(requireContext(), SelectLanguage::class.java)
            intent.putExtra("from", "camera")
            languagePickerLauncher2.launch(intent)
        }

        binding.mic2.setOnClickListener {
            side = SIDE.Listener
            checkPermissionAndStart()
        }
        binding.mic1.setOnClickListener {
            side = SIDE.Speaker
            checkPermissionAndStart()
        }

        binding.SpeakerSpeaker.setOnClickListener {
            speakText(binding.textViewSpeakerSide.text.toString())
        }
        binding.ListenerSpeaker.setOnClickListener {
            speakText(binding.textViewListenerSide.text.toString())
        }

        binding.historButtonChats.setOnClickListener {
            startActivity(Intent(requireContext(), History::class.java))
        }
    }

    private fun startSpeechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        if(side == SIDE.Speaker){
            val langCode = languageCodeMap[binding.languageSpeakerSide.text.toString()] ?: "en-US"
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, langCode)
        }else if(side == SIDE.Listener){
            val langCode = languageCodeMap[binding.ListenerLanguage.text.toString()] ?: "en-US"
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, langCode)
        }
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")

        try {
            speechRecognizerLauncher.launch(intent)
        } catch (e: Exception) {
            if (side == SIDE.Listener) {
                binding.textViewListenerSide.text =
                    "Speech recognition not supported on this device"
                return
            } else {
                binding.textViewSpeakerSide.text = "Speech recognition not supported on this device"
            }


        }
    }

    private fun checkPermissionAndStart() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                startSpeechToText()
            }

            else -> {
                requestMicPermission.launch(android.Manifest.permission.RECORD_AUDIO)
            }
        }
    }
    private fun speakText(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utteranceId")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.ENGLISH // or Locale.ENGLISH etc.
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tts?.stop()
        tts?.shutdown()
    }

}

object SIDE {
    val Speaker: String = "speaker"
    val Listener: String = "listener"
}


val languageCodeMap = mapOf(
    "Afrikaans" to "af-ZA",
    "Albanian" to "sq-AL",
    "Arabic" to "ar-SA",
    "Bengali" to "bn-IN",
    "Bulgarian" to "bg-BG",
    "Catalan" to "ca-ES",
    "Chinese" to "zh-CN",
    "Croatian" to "hr-HR",
    "Czech" to "cs-CZ",
    "Danish" to "da-DK",
    "Dutch" to "nl-NL",
    "English" to "en-US",
    "Estonian" to "et-EE",
    "Finnish" to "fi-FI",
    "French" to "fr-FR",
    "Galician" to "gl-ES",
    "German" to "de-DE",
    "Greek" to "el-GR",
    "Gujarati" to "gu-IN",
    "Hebrew" to "he-IL",
    "Hindi" to "hi-IN",
    "Hungarian" to "hu-HU",
    "Icelandic" to "is-IS",
    "Indonesian" to "id-ID",
    "Italian" to "it-IT",
    "Japanese" to "ja-JP",
    "Kannada" to "kn-IN",
    "Korean" to "ko-KR",
    "Latvian" to "lv-LV",
    "Lithuanian" to "lt-LT",
    "Macedonian" to "mk-MK",
    "Malay" to "ms-MY",
    "Marathi" to "mr-IN",
    "Norwegian" to "no-NO",
    "Persian" to "fa-IR",
    "Polish" to "pl-PL",
    "Portuguese" to "pt-PT",
    "Romanian" to "ro-RO",
    "Russian" to "ru-RU",
    "Slovak" to "sk-SK",
    "Slovenian" to "sl-SI",
    "Spanish" to "es-ES",
    "Swedish" to "sv-SE",
    "Swahili" to "sw-TZ",
    "Tamil" to "ta-IN",
    "Telugu" to "te-IN",
    "Thai" to "th-TH",
    "Turkish" to "tr-TR",
    "Ukrainian" to "uk-UA",
    "Urdu" to "ur-PK",
    "Vietnamese" to "vi-VN",
    "Welsh" to "cy-GB"
)

