package com.example.frontend.presentation.view

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.frontend.R
import com.example.frontend.databinding.FragmentFargCameraBinding
import com.example.frontend.databinding.FragmentFragHomeBinding
import com.example.frontend.presentation.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class fragHome : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentFragHomeBinding? = null
    private val binding get() = _binding!!

    private val speechRecognizerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val matches = result.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (!matches.isNullOrEmpty()) {
                    binding.originalTextFragHome.setText(matches[0])
                }
            }
        }
    private var isPowerOn = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFragHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mic1HomeFrag.setOnClickListener {
            startSpeechToText()
        }


        binding.translateBtnHomeFrag.setOnClickListener {
            val intent = Intent(requireContext(), Translation::class.java)
            intent.putExtra("content",   binding.originalTextFragHome.text.toString())
            intent.putExtra(
                "targetLang",
                view.findViewById<TextView>(R.id.toLanguageText).text.toString()
            )
            startActivity(intent)
        }

        binding.settingsBtn.setOnClickListener {
            startActivity(Intent(requireContext(), Settings::class.java))
        }

        binding.originalTextFragHome.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
            }

            override fun onTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
                if (p0.toString().isNotEmpty()) {
                    binding.mic1HomeFrag.visibility = View.INVISIBLE
                    binding.translateBtnHomeFrag.visibility = View.VISIBLE
                } else {
                    binding.mic1HomeFrag.visibility = View.VISIBLE
                    binding.translateBtnHomeFrag.visibility = View.INVISIBLE
                }

            }

            override fun afterTextChanged(p0: Editable?) {


            }
        })

        binding.toggleButton.setOnClickListener {
            isPowerOn = !isPowerOn

            if (isPowerOn) {
                binding.toggleButton.setBackgroundResource(R.drawable.bg_toggle_on)
                binding.powerText.text = "ON"
                binding.powerIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.appBlue))
                binding.toggleButton.rotation = 180f
                binding.powerIcon.rotation = 180f
                binding.powerText.rotation = 180f
            } else {
                binding.toggleButton.setBackgroundResource(R.drawable.bg_togglwe)
                binding.powerText.text = "OFF"
                binding.powerIcon.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.appOrange
                    )
                )
                binding.toggleButton.rotation = 0f
                binding.powerIcon.rotation = 0f
                binding.powerText.rotation = 0f
            }
        }


        view.findViewById<LinearLayout>(R.id.to_lanugage_home).setOnClickListener {
            val intent = Intent(requireContext(), SelectLanguage::class.java)
            startActivity(intent)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
    private fun startSpeechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        val langCode = "en-US"
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, langCode)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")

        try {
            speechRecognizerLauncher.launch(intent)
        } catch (e: Exception) {
            Log.d("ERRORRR" , e.message.toString())
        }
    }
}

