package com.example.frontend.presentation.view

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frontend.R
import com.example.frontend.presentation.adapters.SettingsAdapter
import com.example.frontend.databinding.ActivitySettingsBinding
import com.example.frontend.domain.model.ItemData

class Settings : AppCompatActivity() {
    private lateinit var binding : ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this , R.color.appBackground)

        binding.backBtnSettings.setOnClickListener {
            finish()
        }

        val settingsItems = listOf<ItemData>(
            ItemData(R.drawable.night_mode, "App Theme", "Tap to Change", true),
            ItemData(R.drawable.offline, "Offline Translation", "Tap to Change", true),
            ItemData(R.drawable.change_lang, "Change Language", "Tap to Change", false),
            ItemData(
                R.drawable.bookmark,
                "Bookmarks",
                "Tap to Change",
                false,
                Bookmarks::class.java
            ),
            ItemData(R.drawable.history, "History", "Tap to Change", false, History::class.java),
            ItemData(R.drawable.rate, "Rate Us", "Tap to Change", false),
            ItemData(R.drawable.share, "Share App", "Tap to Change", false),
            ItemData(R.drawable.customersupport, "Customer Support", "Tap to Change", false),
            ItemData(R.drawable.about, "About Us", "Tap to Change", false),

        )

        val adapter = SettingsAdapter(settingsItems, this)
        binding.recyclerViewSettings.adapter = adapter
        binding.recyclerViewSettings.layoutManager = LinearLayoutManager(this)


    }
}