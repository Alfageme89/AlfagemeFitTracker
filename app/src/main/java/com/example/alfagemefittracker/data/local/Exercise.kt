package com.example.alfagemefittracker.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercises",
    // Un índice acelera las búsquedas por workoutId
    indices = [Index(value = ["workoutId"])],
    // Define la relación entre Exercise y Workout
    foreignKeys = [
        ForeignKey(
            entity = Workout::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE // Si se borra un Workout, se borran sus Exercises
        )
    ]
)
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val reps: Int,
    val weight: Double,
    // Esta es la columna que conecta con la tabla 'workouts'
    val workoutId: Int
)
