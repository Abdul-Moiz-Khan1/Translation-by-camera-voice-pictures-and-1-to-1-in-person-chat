package com.example.frontend.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.frontend.domain.model.BookmarkItem
import com.example.frontend.domain.model.HistoryItem

@Database(entities = [HistoryItem::class, BookmarkItem::class], version = 1)
abstract class HistoryDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun bookmarkDao(): BookmarkDao
}
