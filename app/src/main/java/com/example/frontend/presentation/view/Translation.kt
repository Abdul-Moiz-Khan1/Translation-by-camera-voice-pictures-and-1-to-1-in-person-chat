package com.example.frontend.presentation.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
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
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
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
        window.statusBarColor = ContextCompat.getColor(this , R.color.white)
        val content = intent.getStringExtra("content")
        val targetLanguage = intent.getStringExtra("targetLang")
        buildAd()
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
            viewModel.speak(binding.originalText.text.toString())
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

    private fun buildAd() {
        val builder = AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110") // test ID

        builder.forNativeAd { nativeAd ->
            val adView = layoutInflater.inflate(R.layout.native_ad_simple, null) as NativeAdView

            adView.headlineView = adView.findViewById(R.id.ad_headline)
            adView.bodyView = adView.findViewById(R.id.ad_body)
            adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)

            (adView.headlineView as TextView).text = nativeAd.headline
            (adView.bodyView as TextView).text = nativeAd.body ?: ""
            (adView.callToActionView as Button).apply {
                text = nativeAd.callToAction
                visibility = if (nativeAd.callToAction != null) View.VISIBLE else View.GONE
            }

            adView.setNativeAd(nativeAd)

            val adContainer = findViewById<FrameLayout>(R.id.ad_container_native)
            adContainer.removeAllViews()
            adContainer.addView(adView)

        }
        val adLoader = builder.build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
}