package com.example.frontend.presentation.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frontend.presentation.adapters.BookmarkAdapter
import com.example.frontend.databinding.ActivityBookmarksBinding
import com.example.frontend.domain.model.BookmarkItem
import com.example.frontend.presentation.viewModel.HomeViewModel

class Bookmarks : AppCompatActivity() {
    private val viewModel:HomeViewModel by viewModels()
    private lateinit var binding: ActivityBookmarksBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookmarksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtnBookmarks.setOnClickListener {
            finish()
        }

//        binding.bookmarkRecView.adapter = BookmarkAdapter(bookmarksList)
        binding.bookmarkRecView.layoutManager = LinearLayoutManager(this)
    }
}