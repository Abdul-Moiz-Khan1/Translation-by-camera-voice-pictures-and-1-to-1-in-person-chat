package com.example.frontend.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.frontend.data.local.BookmarkDao
import com.example.frontend.data.local.HistoryDao
import com.example.frontend.data.local.HistoryDatabase

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@dagger.hilt.android.qualifiers.ApplicationContext context: Context): HistoryDatabase {
        return Room.databaseBuilder(
            context,
            HistoryDatabase::class.java,
            "history_db"
        ).build()
    }

    @Provides
    fun provideHistoryDao(database: HistoryDatabase): HistoryDao {
        return database.historyDao()
    }

    @Provides
    fun provideBookmarkDao(database: HistoryDatabase): BookmarkDao {
        return database.bookmarkDao()
    }


}