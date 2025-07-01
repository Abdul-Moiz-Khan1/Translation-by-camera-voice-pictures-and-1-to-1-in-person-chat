package com.example.frontend.domain.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.frontend.data.local.BookmarkDao
import com.example.frontend.data.local.HistoryDao
import com.example.frontend.domain.model.BookmarkItem
import com.example.frontend.domain.model.HistoryItem
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TranslationRepository @Inject constructor(
    private val historyDao: HistoryDao , private val bookmarkDao: BookmarkDao
) {

    fun translateText(
        text: String,
        targetLangCode: String,
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        Log.d("Translation_repo", "content: $text , targetLanguage: $targetLangCode")
        val code = languageCodeMap[targetLangCode] ?: "en"

        getLanguageCode(
            text,
            onResult = { detectedLang ->
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(detectedLang)
                    .setTargetLanguage(code)
                    .build()
                Log.d("Translation_repo", "detectedlang: $detectedLang")

                val translator = Translation.getClient(options)
                val conditions = DownloadConditions.Builder().build()

                translator.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener {
                        Log.d("Translation_repo", "modelDownloaded")

                        translator.translate(text)
                            .addOnSuccessListener { translatedText ->
                                Log.d("Translation_repo", "Translated text: $translatedText")
                                onResult(translatedText)
                            }
                            .addOnFailureListener { e ->
                                Log.d("Translation_repo_trans_fail", e.toString())

                                onError("Translation failed: ${e.localizedMessage}")
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.d("Translation_repo_down_fail", e.toString())
                        onError("Model download failed: ${e.localizedMessage}")
                    }
            },
            onError = {
                Log.d("Translation_repo_onError", it.toString())
                onError("Language detection failed: $it")
            }
        )
    }


    fun recognizeTextFromUri(context: Context, uri: Uri): LiveData<String> {
        val result = MutableLiveData<String>()

        try {
            val image = InputImage.fromFilePath(context, uri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    result.postValue(visionText.text)
                }
                .addOnFailureListener { e ->
                    result.postValue("Error: ${e.message}")
                }

        } catch (e: Exception) {
            result.postValue("Exception: ${e.message}")
        }

        return result
    }

    suspend fun saveToHistory(item: HistoryItem) {
        historyDao.insert(item)
    }

    fun getHistory(): Flow<List<HistoryItem>> {
        return historyDao.getAllHistory()
    }

    suspend fun clearHistory() {
        historyDao.clearHistory()
    }

    suspend fun addBookMark(item: BookmarkItem) {
        bookmarkDao.insert(item)
    }

    suspend fun getBookmarks(): List<BookmarkItem> {
        return bookmarkDao.getAllBookmarks()
    }



}

fun getLanguageCode(text: String, onResult: (String) -> Unit, onError: (String) -> Unit = {}) {
    val languageIdentifier = LanguageIdentification.getClient()
    languageIdentifier.identifyLanguage(text)
        .addOnSuccessListener { languageCode ->
            if (languageCode == "und") {
                onError("Can't identify language.")
            } else {
                onResult(languageCode)
            }
        }
        .addOnFailureListener {
            onError(it.localizedMessage ?: "Unknown error")
        }
}

val languageCodeMap = mapOf(
    "English" to "en",
    "Urdu" to "ur",
    "French" to "fr",
    "German" to "de",
    "Spanish" to "es",
    "Arabic" to "ar",
    "Hindi" to "hi",
    "Chinese" to "zh",
    "Korean" to "ko",
    "Japanese" to "ja",
    "Russian" to "ru",
    "Portuguese" to "pt",
    "Italian" to "it",
    "Turkish" to "tr",
    "Bengali" to "bn",
    "Polish" to "pl",
    "Dutch" to "nl",
    "Vietnamese" to "vi",
    "Thai" to "th",
    "Swedish" to "sv",
    "Indonesian" to "id",
    "Greek" to "el",
    "Hebrew" to "he",
    "Malay" to "ms",
    "Romanian" to "ro",
    "Czech" to "cs",
    "Hungarian" to "hu",
    "Finnish" to "fi",
    "Danish" to "da",
    "Norwegian" to "no",
    "Filipino" to "tl",
    "Tamil" to "ta",
    "Telugu" to "te",
    "Marathi" to "mr",
    "Gujarati" to "gu",
    "Ukrainian" to "uk",
    "Slovak" to "sk",
    "Croatian" to "hr",
    "Bulgarian" to "bg",
    "Persian" to "fa",
    "Afrikaans" to "af",
    "Albanian" to "sq",
    "Belarusian" to "be",
    "Catalan" to "ca",
    "Esperanto" to "eo",
    "Estonian" to "et",
    "Galician" to "gl",
    "Georgian" to "ka",
    "Haitian Creole" to "ht",
    "Icelandic" to "is",
    "Irish" to "ga",
    "Lithuanian" to "lt",
    "Latvian" to "lv",
    "Macedonian" to "mk",
    "Maltese" to "mt",
    "Swahili" to "sw",
    "Tagalog" to "tl",
    "Welsh" to "cy"
)
