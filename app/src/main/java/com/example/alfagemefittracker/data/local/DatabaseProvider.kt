package com.example.alfagemefittracker.data.local

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "workout_database"
            )
            .fallbackToDestructiveMigration() // Esto evita el crash al cambiar la estructura
            .build()
            INSTANCE = instance
            instance
        }
    }
}
