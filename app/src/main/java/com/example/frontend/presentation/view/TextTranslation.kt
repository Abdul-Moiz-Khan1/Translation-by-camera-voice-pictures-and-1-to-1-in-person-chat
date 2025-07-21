package com.example.frontend.presentation.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.frontend.R
import com.example.frontend.presentation.view.Translation
import com.example.frontend.databinding.ActivityTextTranslationBinding
import com.example.frontend.presentation.service.MyScreenshotService
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class TextTranslation : AppCompatActivity() {

    private lateinit var mediaProjectionManager: MediaProjectionManager
    private val REQUEST_CODE_SCREEN_CAPTURE = 1001
    private lateinit var binding: ActivityTextTranslationBinding
    private val speechRecognizerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val matches = result.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (!matches.isNullOrEmpty()) {
                    binding.typedText.setText(matches[0])
                }
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTextTranslationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this , R.color.white)

        mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        binding.mediumBannerAdView.loadAd(adRequest)
        binding = ActivityTextTranslationBinding.inflate(layoutInflater)
//        binding.captureButton.setOnClickListener {
//            val captureIntent = mediaProjectionManager.createScreenCaptureIntent()
//            startActivityForResult(captureIntent, REQUEST_CODE_SCREEN_CAPTURE)
//        }

        val textToEdit = intent.getStringExtra("text_to_edit") ?: intent.getStringExtra("content")

        binding.typedText.setText(textToEdit)
        val to_language = intent.getStringExtra("toLanguage")
        binding.toLanguageTextTranslator.setText(to_language)
        binding.language.setText(binding.fromlangTextTranslator.text)

        binding.micbtn123.setOnClickListener {
            startSpeechToText()
        }
        binding.backBtnTranslate.setOnClickListener {
            finish()
        }
        binding.fromlangTextTranslator.setOnClickListener {
            startActivity(Intent(this, SelectLanguage::class.java))
        }
        binding.toLanguageTextTranslator.setOnClickListener {
            startActivity(Intent(this, SelectLanguage::class.java))
        }

        binding.typedText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                if(count>0){
                    binding.translateBtn.visibility = View.VISIBLE
                    binding.clearBtnTextTrans.visibility = View.VISIBLE
                    binding.micbtn123.visibility = View.INVISIBLE
                }
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    binding.translateBtn.visibility = View.INVISIBLE
                    binding.clearBtnTextTrans.visibility = View.INVISIBLE
                    binding.micbtn123.visibility = View.VISIBLE
                } else {
                    binding.micbtn123.visibility = View.INVISIBLE
                    binding.clearBtnTextTrans.visibility = View.VISIBLE
                    binding.translateBtn.visibility = View.VISIBLE
                }
            }

        })
        binding.clearBtnTextTrans.setOnClickListener {
            binding.typedText.setText("")
        }

        binding.translateBtn.setOnClickListener {
            val intent = Intent(this, Translation::class.java)
            intent.putExtra("content", binding.typedText.text.toString())
            intent.putExtra("targetLang", to_language)
            startActivity(intent)
            finish()
        }

        binding.backBtnTranslate.setOnClickListener {
            finish()
        }


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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SCREEN_CAPTURE && resultCode == RESULT_OK && data != null) {
            val serviceIntent = Intent(this, MyScreenshotService::class.java).apply {
                putExtra("code", resultCode)
                putExtra("data", data)
            }
            ContextCompat.startForegroundService(this, serviceIntent)
        }
    }
}