package com.example.frontend.presentation.viewModel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class OCRViewModel : ViewModel() {

    private val _recognizedText = MutableLiveData<String>()
    val recognizedText: LiveData<String> get() = _recognizedText


    private val _translatedText = MutableLiveData<String>()
    val translatedText: LiveData<String> get() = _translatedText

    fun extractTextFromBitmap(bitmap: Bitmap) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val inputImage = InputImage.fromBitmap(bitmap, 0)

        recognizer.process(inputImage)
            .addOnSuccessListener { result ->
                _recognizedText.postValue(result.text)
            }
            .addOnFailureListener { e ->
                _recognizedText.postValue("OCR failed: ${e.message}")
            }
    }

    fun translateText(sourceLang: String = TranslateLanguage.ENGLISH, targetLang: String = TranslateLanguage.URDU) {
        val sourceText = _recognizedText.value ?: return

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLang)
            .setTargetLanguage(targetLang)
            .build()

        val translator = Translation.getClient(options)

        translator.downloadModelIfNeeded()
            .addOnSuccessListener {
                translator.translate(sourceText)
                    .addOnSuccessListener { translated ->
                        _translatedText.postValue(translated)
                    }
                    .addOnFailureListener { e ->
                        _translatedText.postValue("Translation failed: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                _translatedText.postValue("Model download failed: ${e.message}")
            }
    }
    fun reset() {
        _recognizedText.postValue("")
        _translatedText.postValue("")
    }
}
