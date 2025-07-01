package com.example.frontend.presentation.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.domain.model.BookmarkItem
import com.example.frontend.domain.model.HistoryItem
import com.example.frontend.domain.repository.TranslationRepository
import com.example.frontend.domain.repository.languageCodeMap
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TranslationRepository
) : ViewModel() {

    private val _translatedText = MutableLiveData<String>()
    val translatedText: LiveData<String> get() = _translatedText

    private val _ocrText = MutableLiveData<String>()
    val ocrText: LiveData<String> get() = _ocrText

    fun translateText(text: String, targetLangCode: String) {
        Log.d("Translation_viewmodel", "content: $text , targetLanguage: $targetLangCode")

        repository.translateText(
            text,
            targetLangCode,
            onResult = {
                _translatedText.value = it
                viewModelScope.launch {
                    repository.saveToHistory(
                        HistoryItem(
                            0,
                            language = targetLangCode,
                            originalText = text,
                            translatedText = it
                        )
                    )
                }
            },
            onError = { _translatedText.value = it }
        )
    }

    suspend fun getHistoryOnce(): List<HistoryItem> {
        return repository.getHistory().first()
    }

    suspend fun AddBookmark(item: BookmarkItem) {
        repository.addBookMark(item)
    }

    suspend fun getBookmarks(): List<BookmarkItem> {
        return repository.getBookmarks()
    }

    fun processImageAndTranslate(
        context: Context,
        imageUri: Uri,
        targetLang: String,
        callback: (Bitmap, String?, String?) -> Unit
    ) {
        val inputImage = InputImage.fromFilePath(context, imageUri)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                val originalBitmap =
                    MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
                val mutableBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
                val canvas = Canvas(mutableBitmap)

                val bgPaint = Paint().apply {
                    color = Color.WHITE
                    style = Paint.Style.FILL
                }

                val textPaint = Paint().apply {
                    color = Color.BLACK
                    textSize = 40f
                    isAntiAlias = true
                }

                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ENGLISH)
                    .setTargetLanguage(
                        languageCodeMap[targetLang] ?: "en"
                    )
                    .build()

                val translator = Translation.getClient(options)
                translator.downloadModelIfNeeded()
                    .addOnSuccessListener {
                        val allTranslations = mutableListOf<Pair<String, Rect?>>()
                        val lines = visionText.textBlocks.flatMap { it.lines }

                        fun processNextLine(index: Int) {
                            if (index >= lines.size) {
                                for ((translated, box) in allTranslations) {
                                    box?.let {
                                        canvas.drawRect(it, bgPaint)
                                        canvas.drawText(
                                            translated,
                                            it.left.toFloat(),
                                            it.bottom.toFloat() - 10,
                                            textPaint
                                        )
                                    }
                                }

                                val fullTranslatedText =
                                    allTranslations.joinToString("\n") { it.first }
                                callback(mutableBitmap, visionText.text, fullTranslatedText)
                                return
                            }

                            val line = lines[index]
                            translator.translate(line.text)
                                .addOnSuccessListener { translatedText ->
                                    allTranslations.add(Pair(translatedText, line.boundingBox))
                                    processNextLine(index + 1)
                                }
                                .addOnFailureListener {
                                    allTranslations.add(
                                        Pair(
                                            line.text,
                                            line.boundingBox
                                        )
                                    ) // fallback
                                    processNextLine(index + 1)
                                }
                        }

                        processNextLine(0)
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            context,
                            "Failed to download translation model",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(context, "OCR failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

}
