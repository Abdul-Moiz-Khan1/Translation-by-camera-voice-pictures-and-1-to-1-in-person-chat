package com.example.frontend

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.frontend.databinding.ActivityTextTranslationBinding

class TextTranslation : AppCompatActivity() {
    private lateinit var binding: ActivityTextTranslationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextTranslationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE

        val language = intent.getStringExtra("toLanguage")
        binding.fromlangtextTranslator.setText(language)
        binding.language.setText(language)

        val typedText = findViewById<EditText>(R.id.typedText)

        if (typedText.text.toString() != "") {
            findViewById<TextView>(R.id.translateBtn).visibility = View.VISIBLE
            findViewById<LinearLayout>(R.id.micBtn).visibility = View.GONE
        }

        findViewById<Button>(R.id.translateBtn).setOnClickListener {
            val intent = Intent(this, Translation::class.java)
            intent.putExtra("content", typedText.text.toString())
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.backBtnTranslate).setOnClickListener {
            finish()
        }


    }
}