package com.example.frontend.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.frontend.domain.model.BookmarkItem
import com.example.frontend.domain.model.HistoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: BookmarkItem)

    @Query("SELECT * FROM bookmark_table")
    fun getAllBookmarks(): List<BookmarkItem>

}