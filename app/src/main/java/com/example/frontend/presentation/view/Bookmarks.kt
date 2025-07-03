package com.example.frontend.presentation.view

import android.graphics.Color
import android.os.Bundle
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
}