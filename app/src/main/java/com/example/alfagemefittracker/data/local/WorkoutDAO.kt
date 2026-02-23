package com.example.alfagemefittracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    // Al insertar un workout, si ya existe, se reemplaza
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: Workout)

    // Query para obtener todos los workouts, ordenados por id descendente
    @Query("SELECT * FROM workouts ORDER BY id DESC")
    fun getAllWorkouts(): Flow<List<Workout>>

    @Update
    suspend fun updateWorkout(workout: Workout)

    @Delete
    suspend fun deleteWorkout(workout: Workout)
}
