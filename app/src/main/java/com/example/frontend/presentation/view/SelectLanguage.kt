package com.example.frontend.presentation.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frontend.R
import com.example.frontend.presentation.adapters.LanguageAdapter
import com.example.frontend.databinding.ActivitySelectLanguageBinding

class SelectLanguage : AppCompatActivity() {
    private lateinit var adapter: LanguageAdapter


    private lateinit var binding: ActivitySelectLanguageBinding
    private var selectedLanguage: String? = null
    private var selectedPosition = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySelectLanguageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

        window.statusBarColor = ContextCompat.getColor(this , R.color.white)
        setContentView(binding.root)

        val from = intent.getStringExtra("from")

             adapter = LanguageAdapter(languages) { language, position ->
                 selectedLanguage = language
                 selectedPosition = position
                 binding.confirmButton.visibility = View.VISIBLE
             }

        binding.languageRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.languageRecyclerView.adapter = adapter

        binding.searchBtn.setOnClickListener {
            binding.confirmButton.visibility = View.VISIBLE
            binding.searchView.visibility = View.VISIBLE
            binding.languageTitle.visibility = View.GONE
            binding.searchBtn.visibility = View.INVISIBLE
            binding.searchView.requestFocus()
        }

        binding.searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                adapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.confirmButton.setOnClickListener {
            if(from == "camera"){
                val intent = Intent()
                intent.putExtra("selected_language_for_camera", selectedLanguage)
                setResult(RESULT_OK, intent)
                finish()
            }
            else if (selectedLanguage != null) {
                val intent = Intent()
                intent.putExtra("SELECTED_LANGUAGE", selectedLanguage)
                setResult(RESULT_OK, intent)
                val intent2 = Intent(this, TextTranslation::class.java)
                intent2.putExtra("toLanguage", selectedLanguage)
                startActivity(intent2)
                finish()

            } else {
                Toast.makeText(this, "Please select a language", Toast.LENGTH_SHORT).show()
            }
        }

        binding.backButton.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

    }
    private val languages = listOf(
        "Afrikaans", "Albanian", "Arabic", "Belarusian", "Bulgarian", "Bengali", "Catalan", "Chinese",
        "Croatian", "Czech", "Danish", "Dutch", "English", "Esperanto", "Estonian", "Finnish", "French",
        "Galician", "Georgian", "German", "Greek", "Gujarati", "Haitian Creole", "Hebrew", "Hindi",
        "Hungarian", "Icelandic", "Indonesian", "Irish", "Italian", "Japanese", "Kannada", "Korean",
        "Lithuanian", "Latvian", "Macedonian", "Marathi", "Malay", "Maltese", "Norwegian", "Persian",
        "Polish", "Portuguese", "Romanian", "Russian", "Slovak", "Slovenian", "Spanish", "Swedish",
        "Swahili", "Tagalog", "Tamil", "Telugu", "Thai", "Turkish", "Ukrainian", "Urdu", "Vietnamese",
        "Welsh"
    )

}