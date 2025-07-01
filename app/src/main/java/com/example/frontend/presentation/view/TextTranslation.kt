package com.example.frontend.presentation.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.frontend.R
import com.example.frontend.presentation.view.Translation
import com.example.frontend.databinding.ActivityTextTranslationBinding

class TextTranslation : AppCompatActivity() {
    private lateinit var binding: ActivityTextTranslationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextTranslationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE

        val content = intent.getStringExtra("content")
        binding.typedText.setText(content)
        val to_language = intent.getStringExtra("toLanguage")
        binding.toLanguageTextTranslator.setText(to_language)
        binding.language.setText(binding.fromlangTextTranslator.text)

        val typedText = findViewById<EditText>(R.id.typedText)

        binding.fromlangTextTranslator.setOnClickListener {
            startActivity(Intent(this, SelectLanguage::class.java))
        }
        binding.toLanguageTextTranslator.setOnClickListener {
            startActivity(Intent(this, SelectLanguage::class.java))
        }

        binding.typedText.addTextChangedListener(object : TextWatcher {
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
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    binding.translateBtn.visibility = View.INVISIBLE
                    binding.clearBtnTextTrans.visibility = View.INVISIBLE
                    binding.micbtn123.visibility = View.VISIBLE
                } else {
                    binding.micbtn123.visibility = View.INVISIBLE
                    binding.clearBtnTextTrans.visibility = View.VISIBLE
                    binding.translateBtn.visibility = View.VISIBLE
                }
            }

        })
        binding.clearBtnTextTrans.setOnClickListener {
            binding.typedText.setText("")
        }

        binding.translateBtn.setOnClickListener {
            val intent = Intent(this, Translation::class.java)
            intent.putExtra("content", typedText.text.toString())
            intent.putExtra("targetLang", to_language)
            startActivity(intent)
            finish()
        }

        binding.backBtnTranslate.setOnClickListener {
            finish()
        }


    }
}