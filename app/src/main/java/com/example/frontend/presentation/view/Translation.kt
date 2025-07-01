package com.example.frontend.presentation.view

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.frontend.databinding.ActivityTranslationBinding
import com.example.frontend.domain.model.BookmarkItem
import com.example.frontend.presentation.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class Translation : AppCompatActivity() {

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var binding: ActivityTranslationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTranslationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE

        val content = intent.getStringExtra("content")
        val targetLanguage = intent.getStringExtra("targetLang")

        Log.d("Translation_origin", "content: $content , targetLanguage: $targetLanguage")
        viewModel.translateText(content.toString(), targetLanguage.toString())
        viewModel.translatedText.observe(this) { translatedText ->
            binding.translatedText.setText(translatedText)
        }

        binding.originalText.setText(content)

        binding.backBtnTranslateFin.setOnClickListener {
            finish()
        }
        binding.clear.setOnClickListener {
            binding.originalText.setText("")
        }
        binding.edit.setOnClickListener {
            binding.originalText.isEnabled = true
            binding.originalText.requestFocus()
        }
        binding.copyoriginal.setOnClickListener {
            Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
        }
        binding.copytrans.setOnClickListener {
            Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
        }
        binding.speak.setOnClickListener {
            Toast.makeText(this, "Speaking", Toast.LENGTH_SHORT).show()
        }
        binding.fullscreen.setOnClickListener {
            Toast.makeText(this, "Fullscreen", Toast.LENGTH_SHORT).show()
        }
        binding.bookmark.setOnClickListener {
            lifecycleScope.launch {
                viewModel.AddBookmark(
                    BookmarkItem(
                        0,
                        binding.originalText.text.toString(),
                        binding.translatedText.text.toString()
                    )
                )
            }
        }


    }
}