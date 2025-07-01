package com.example.frontend.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "history_table")
data class HistoryItem(
   @PrimaryKey(autoGenerate = true)  val id: Int = 0,
    val language: String,
    val originalText: String,
    val translatedText: String
)