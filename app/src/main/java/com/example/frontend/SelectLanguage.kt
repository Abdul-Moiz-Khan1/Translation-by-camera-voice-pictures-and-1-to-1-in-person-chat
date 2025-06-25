package com.example.frontend

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SelectLanguage : AppCompatActivity() {

    private var selectedLanguage: String? = null
    private var selectedPosition = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.WHITE
        setContentView(R.layout.activity_select_language)
        val recyclerView = findViewById<RecyclerView>(R.id.languageRecyclerView)
        val confirmButton = findViewById<Button>(R.id.confirmButton)
        val backButton = findViewById<ImageView>(R.id.backButton)

        val adapter = LanguageAdapter(languages) { language, position ->
            selectedLanguage = language
            selectedPosition = position
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        confirmButton.setOnClickListener {
            if (selectedLanguage != null) {
                val intent = Intent()
                intent.putExtra("SELECTED_LANGUAGE", selectedLanguage)
                setResult(Activity.RESULT_OK, intent)
                val intent2 = Intent(this, TextTranslation::class.java)
                intent2.putExtra("toLanguage", selectedLanguage)
                startActivity(intent2)
                finish()

            } else {
                Toast.makeText(this, "Please select a language", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

    }
    private val languages = listOf(
        "English", "Urdu", "French", "German", "Spanish", "Arabic", "Hindi", "Chinese", "Korean", "Japanese",
        "Russian", "Portuguese", "Italian", "Turkish", "Bengali", "Polish", "Dutch", "Vietnamese", "Thai", "Swedish",
        "Indonesian", "Greek", "Hebrew", "Malay", "Romanian", "Czech", "Hungarian", "Finnish", "Danish", "Norwegian",
        "Filipino", "Tamil", "Telugu", "Punjabi", "Marathi", "Gujarati", "Ukrainian", "Slovak", "Serbian", "Croatian",
        "Bulgarian", "Persian", "Pashto", "Sinhala", "Swahili", "Zulu", "Malayalam", "Latvian", "Estonian", "Icelandic"
    )

}