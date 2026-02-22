
package com.example.alfagemefittracker.data.repository

import com.example.alfagemefittracker.data.local.Workout
import com.example.alfagemefittracker.data.local.WorkoutDao
import com.example.alfagemefittracker.data.remote.ExerciseApiService
import com.example.alfagemefittracker.data.remote.dto.ExerciseDto
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(
    private val workoutDao: WorkoutDao,
    private val exerciseApiService: ExerciseApiService
) {

    fun getWorkouts(): Flow<List<Workout>> =
        workoutDao.getAllWorkouts()

    suspend fun insertWorkout(workout: Workout) =
        workoutDao.insertWorkout(workout)

    suspend fun deleteWorkout(workout: Workout) =
        workoutDao.deleteWorkout(workout)

    suspend fun getExercisesFromApi(): List<ExerciseDto> {
        return try {
            exerciseApiService.getExercises()
        } catch (e: Exception) {
            // In a real app, you'd handle this error more gracefully
            emptyList()
        }
    }
}
