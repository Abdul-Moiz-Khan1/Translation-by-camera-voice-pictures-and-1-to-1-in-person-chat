package com.example.frontend.domain.model

import android.app.Activity

data class ItemData(
    val titleImg: Int,
    val title: String,
    val description: String,
    val radio: Boolean,
    val targetActivity: Class<out Activity>? = null
)