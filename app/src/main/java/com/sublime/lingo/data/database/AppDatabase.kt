package com.sublime.lingo.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TranslationEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun translationDao(): TranslationDao

    companion object {
        const val DATABASE_NAME = "lingo_database"
    }
}
