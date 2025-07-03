package com.example.frontend.presentation.viewModel

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.domain.model.BookmarkItem
import com.example.frontend.domain.model.HistoryItem
import com.example.frontend.domain.repository.TranslationRepository
import com.example.frontend.domain.repository.languageCodeMap
import com.example.frontend.presentation.view.SIDE
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
    private val application: Application,
    private val repository: TranslationRepository
) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null

    private var isTtsInitialized = false
    init {
        tts = TextToSpeech(application.applicationContext, this)
    }
    override fun onInit(status: Int) {
        isTtsInitialized = status == TextToSpeech.SUCCESS
    }
    fun speak(text: String) {
        if (isTtsInitialized) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utteranceId")
        }
    }
    private val _translatedText = MutableLiveData<String>()
    val translatedText: LiveData<String> get() = _translatedText

    fun translateText(text: String, targetLang: String, onResult: (String) -> Unit) {
        Log.d("Translation_viewmodel", "content: $text , targetLanguage: $targetLang")

        viewModelScope.launch {
            val translated = repository.translateText(
                text,
                targetLang,
                onResult = {
                    _translatedText.value = it
                    viewModelScope.launch {
                        repository.saveToHistory(
                            HistoryItem(
                                0,
                                language = targetLang,
                                originalText = text,
                                translatedText = it
                            )
                        )
                    }

                    onResult(translatedText.value.toString())
                },
                onError = { _translatedText.value = it }
            )
        }
    }


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
                                    )
                                    processNextLine(index + 1)
                                }
                        }

                        processNextLine(0)
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            context,
                            "Failed downlaod",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(context, "OCR failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun handleSpeechInput(
        input: String,
        sourceSide: String,
        speakerLang: String,
        listenerLang: String,
        onTranslated: (speakerText: String, listenerText: String) -> Unit
    ) {
        val targetLang = if (sourceSide == SIDE.Speaker) listenerLang else speakerLang
        repository.translateText(input, targetLang, { translated ->
            if (sourceSide == SIDE.Speaker)
                onTranslated(input, translated)
            else
                onTranslated(translated, input)
        }, {
            Log.e("SpeechTranslate", "Error: $it")
        })
    }

    fun saveBitmapToGallery(context: Context, bitmap: Bitmap, filename: String = "translated_${System.currentTimeMillis()}") {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$filename.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Translations")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        imageUri?.let { uri ->
            resolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)

            Toast.makeText(context, "Saved to gallery!", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
        }
    }


}
fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(value: T) {
            observer.onChanged(value)
            removeObserver(this)
        }
    })
}

