package com.example.frontend

import android.graphics.Color
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.frontend.databinding.ActivityTranslationBinding

class Translation : AppCompatActivity() {
    private lateinit var binding: ActivityTranslationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTranslationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE

        val content = intent.getStringExtra("content")

            binding.originalText.setText(content)
        binding.translatedText.setText(content)
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
            Toast.makeText(this, "Bookmarked", Toast.LENGTH_SHORT).show()
        }


    }
}