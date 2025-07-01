package com.example.frontend.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmark_table")
data class BookmarkItem(
    @PrimaryKey(autoGenerate = true) val id: Int =0,
    val text1: String,
    val text2: String
)