package com.example.frontend

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frontend.adapters.BookmarkAdapter
import com.example.frontend.databinding.ActivityBookmarksBinding
import com.example.frontend.model.BookmarkItem

class Bookmarks : AppCompatActivity() {
    private lateinit var binding: ActivityBookmarksBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookmarksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtnBookmarks.setOnClickListener {
            finish()
        }
        val bookmarksList = listOf(
            BookmarkItem("Text 1", "Text 2"),
            BookmarkItem("Text 3", "Text 4"),
            BookmarkItem("Text 5", "Text 6"),
            BookmarkItem("Text 7", "Text 8"),
            BookmarkItem("Text 1", "Text 2"),
            BookmarkItem("Text 3", "Text 4"),
            BookmarkItem("Text 5", "Text 6"),
            BookmarkItem("Text 3", "Text 4"),
            BookmarkItem("Text 5", "Text 6"),
            BookmarkItem("Text 7", "Text 8"),
            BookmarkItem("Text 1", "Text 2"),
            BookmarkItem("Text 3", "Text 4"),
            BookmarkItem("Text 5", "Text 6"),
            BookmarkItem("Text 3", "Text 4"),
            BookmarkItem("Text 5", "Text 6"),
            BookmarkItem("Text 7", "Text 8"),
            BookmarkItem("Text 1", "Text 2"),
            BookmarkItem("Text 3", "Text 4"),
            BookmarkItem("Text 5", "Text 6"),
            BookmarkItem("Text 3", "Text 4"),
            BookmarkItem("Text 5", "Text 6"),
            BookmarkItem("Text 7", "Text 8"),
            BookmarkItem("Text 1", "Text 2"),
            BookmarkItem("Text 3", "Text 4"),
            BookmarkItem("Text 5", "Text 6"),
            BookmarkItem("Text 7", "Text 8")

        )
        binding.bookmarkRecView.adapter = BookmarkAdapter(bookmarksList)
        binding.bookmarkRecView.layoutManager = LinearLayoutManager(this)
    }
}
