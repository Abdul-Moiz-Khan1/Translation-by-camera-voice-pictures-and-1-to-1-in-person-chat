package com.example.frontend

import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PictureTranslation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_translation)

        findViewById<ImageView>(R.id.backBtnPictureTranslation).setOnClickListener {
            finish()
        }

        findViewById<ImageView>(R.id.copyfin).setColorFilter(ContextCompat.getColor(this , R.color.appBlue), PorterDuff.Mode.SRC_IN)
        findViewById<ImageView>(R.id.fullscreenfin).setColorFilter(ContextCompat.getColor(this , R.color.appBlue), PorterDuff.Mode.SRC_IN)
        findViewById<ImageView>(R.id.bookmarkfin).setColorFilter(ContextCompat.getColor(this , R.color.appBlue), PorterDuff.Mode.SRC_IN)


    }
}