package com.example.frontend.presentation.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frontend.R
import com.example.frontend.presentation.adapters.BookmarkAdapter
import com.example.frontend.databinding.ActivityBookmarksBinding
import com.example.frontend.domain.model.BookmarkItem
import com.example.frontend.presentation.viewModel.HomeViewModel
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAdView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class Bookmarks : AppCompatActivity() {
    private val viewModel:HomeViewModel by viewModels()
    private lateinit var binding: ActivityBookmarksBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookmarksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        buildAd()
        window.statusBarColor = ContextCompat.getColor(this , R.color.white)

        binding.backBtnBookmarks.setOnClickListener {
            finish()
        }

        lifecycleScope.launch {
            val bookmarksList = withContext(Dispatchers.IO) {
                viewModel.getBookmarks()
            }
            binding.bookmarkRecView.adapter = BookmarkAdapter(bookmarksList)
            binding.bookmarkRecView.layoutManager = LinearLayoutManager(this@Bookmarks)
        }

    }
    private fun buildAd() {
        val builder = AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110") // Test native ad ID

        builder.forNativeAd { nativeAd ->
            val adView = layoutInflater.inflate(R.layout.native_ad_with_media, null) as NativeAdView

            // Bind views
            adView.mediaView = adView.findViewById(R.id.ad_media)
            adView.headlineView = adView.findViewById(R.id.ad_headline)
            adView.bodyView = adView.findViewById(R.id.ad_body)
            adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)

            // Set content
            (adView.headlineView as TextView).text = nativeAd.headline
            (adView.bodyView as TextView).text = nativeAd.body ?: ""
            (adView.callToActionView as Button).apply {
                text = nativeAd.callToAction
                visibility = if (nativeAd.callToAction != null) View.VISIBLE else View.GONE
            }

            // Register ad
            adView.setNativeAd(nativeAd)

            // Attach to container
            val adContainer = findViewById<FrameLayout>(R.id.ad_container_native_mediaview)
            adContainer.removeAllViews()
            adContainer.addView(adView)
        }

        val adLoader = builder.build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

}