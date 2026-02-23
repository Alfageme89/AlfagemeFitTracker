package com.example.alfagemefittracker.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_logs",
    foreignKeys = [
        ForeignKey(
            entity = Workout::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE // If a workout is deleted, its logs are also deleted.
        )
    ]
)
data class WorkoutLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val workoutId: Int,
    val exerciseId: String, // From the API
    val exerciseName: String,
    val sets: Int = 0, // For future tracking
    val reps: Int = 0, // For future tracking
    val weight: Double = 0.0 // For future tracking
)
