package com.example.alfagemefittracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Workout::class, Exercise::class, WorkoutLog::class],
    version = 3, // Subimos a la versión 3 para forzar la limpieza
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutLogDao(): WorkoutLogDao
}
