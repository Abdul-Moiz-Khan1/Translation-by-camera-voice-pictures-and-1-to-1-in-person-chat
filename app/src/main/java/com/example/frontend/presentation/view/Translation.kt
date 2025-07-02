package com.example.frontend.presentation.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.frontend.R
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
        window.statusBarColor = ContextCompat.getColor(this , R.color.appBackground)

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
            val intent = Intent(this , TextTranslation::class.java)
            intent.putExtra("text_to_edit" , binding.originalText.text.toString())
            intent.putExtra("toLanguage" , targetLanguage)
            startActivity(intent)
            finish()
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
            Toast.makeText(this, "Bookmarked", Toast.LENGTH_SHORT).show()
        }


    }
}