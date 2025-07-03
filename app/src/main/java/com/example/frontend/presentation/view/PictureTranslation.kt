package com.example.frontend.presentation.view

import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.frontend.R
import com.example.frontend.domain.model.BookmarkItem
import com.example.frontend.presentation.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PictureTranslation : AppCompatActivity() {

    private val viewModel: HomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = ContextCompat.getColor(this , R.color.white)
        setContentView(R.layout.activity_picture_translation)

        findViewById<ImageView>(R.id.backBtnPictureTranslation).setOnClickListener {
            finish()
        }
        val original = intent.getStringExtra("original")
        val translated = intent.getStringExtra("translated")

        findViewById<TextView>(R.id.orignalText_PicTrans).text = original
        findViewById<TextView>(R.id.translatedText_PicTrans).text = translated


        findViewById<ImageView>(R.id.copyfin).setColorFilter(
            ContextCompat.getColor(
                this,
                R.color.appBlue
            ), PorterDuff.Mode.SRC_IN
        )
        findViewById<ImageView>(R.id.fullscreenfin).setColorFilter(
            ContextCompat.getColor(
                this,
                R.color.appBlue
            ), PorterDuff.Mode.SRC_IN
        )
        findViewById<ImageView>(R.id.bookmar_PicTrans).setColorFilter(
            ContextCompat.getColor(
                this,
                R.color.appBlue
            ), PorterDuff.Mode.SRC_IN
        )
        findViewById<ImageView>(R.id.bookmar_PicTrans).setOnClickListener {
            lifecycleScope.launch {
                viewModel.AddBookmark(
                    BookmarkItem(
                        0,
                        findViewById<TextView>(R.id.orignalText_PicTrans).text.toString(),
                        findViewById<TextView>(R.id.translatedText_PicTrans).text.toString(),
                    )
                )
            }
            Toast.makeText(this, "Bookmarked", Toast.LENGTH_SHORT).show()
        }
    }
}