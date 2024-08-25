package com.sublime.lingo.di

import android.content.Context
import androidx.room.Room
import com.sublime.lingo.data.database.AppDatabase
import com.sublime.lingo.data.database.TranslationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase =
        Room
            .databaseBuilder(
                context,
                AppDatabase::class.java,
                AppDatabase.DATABASE_NAME,
            ).build()

    @Provides
    @Singleton
    fun provideTranslationMessageDao(database: AppDatabase): TranslationDao = database.translationDao()
}
