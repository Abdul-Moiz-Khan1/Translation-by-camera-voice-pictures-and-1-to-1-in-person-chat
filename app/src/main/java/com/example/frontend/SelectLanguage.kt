package com.example.frontend

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.frontend.adapters.LanguageAdapter
import com.example.frontend.databinding.ActivitySelectLanguageBinding

class SelectLanguage : AppCompatActivity() {
    private lateinit var adapter: LanguageAdapter

    private lateinit var binding: ActivitySelectLanguageBinding
    private var selectedLanguage: String? = null
    private var selectedPosition = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySelectLanguageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.WHITE
        setContentView(binding.root)

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

        binding.searchView.addTextChangedListener(object : TextWatcher{
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

        binding.backButton.setOnClickListener {
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