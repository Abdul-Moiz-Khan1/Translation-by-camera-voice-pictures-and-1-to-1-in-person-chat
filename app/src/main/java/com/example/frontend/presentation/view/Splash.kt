package com.example.frontend.presentation.view

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.frontend.R
import com.example.frontend.presentation.view.MainActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds

import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        MobileAds.initialize(this) {}
        loadAppOpenAd()
        val splashImage = findViewById<ImageView>(R.id.splash_image)
        ObjectAnimator.ofFloat(splashImage, "alpha", 0.3f, 1f).apply {
            interpolator = android.view.animation.AccelerateDecelerateInterpolator()
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            duration = 2000
            start()
        }

    }

    private var appOpenAd: AppOpenAd? = null
    private var isAdDisplayed: Boolean = false
    private val appOpenAdLoadCallback = object : AppOpenAdLoadCallback() {

        override fun onAdLoaded(ad: AppOpenAd) {
            appOpenAd = ad
            appOpenAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    isAdDisplayed = false
                    startActivity(Intent(this@Splash, MainActivity::class.java))
                    finish()

                }

                override fun onAdShowedFullScreenContent() {
                    isAdDisplayed = true
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    isAdDisplayed = false
                    startActivity(Intent(this@Splash, MainActivity::class.java))
                    finish()

                }
            }

            ad.show(this@Splash)
        }


        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            Log.d("appOpenAd", "Failed to load app open ad: ${loadAdError.message}")
        }
    }

    private fun loadAppOpenAd() {
        val adRequest = AdRequest.Builder().build()
        AppOpenAd.load(
            this,
            "ca-app-pub-3940256099942544/9257395921",
            adRequest,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            appOpenAdLoadCallback
        )
    }

}