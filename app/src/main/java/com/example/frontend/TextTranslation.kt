package com.example.frontend

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.frontend.databinding.ActivityTextTranslationBinding

class TextTranslation : AppCompatActivity() {
    private lateinit var binding: ActivityTextTranslationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextTranslationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE

        val from_language = intent.getStringExtra("fromLanguage")
        val to_language = intent.getStringExtra("toLanguage")
        binding.fromlangTextTranslator.setText(to_language)
        binding.language.setText(to_language)

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
            startActivity(intent)
        }

        binding.backBtnTranslate.setOnClickListener {
            finish()
        }


    }
}