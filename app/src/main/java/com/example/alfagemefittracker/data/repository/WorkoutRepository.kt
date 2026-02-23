
package com.example.alfagemefittracker.data.repository

import com.example.alfagemefittracker.data.local.Workout
import com.example.alfagemefittracker.data.local.WorkoutDao
import com.example.alfagemefittracker.data.local.WorkoutLog
import com.example.alfagemefittracker.data.local.WorkoutLogDao
import com.example.alfagemefittracker.data.remote.ExerciseApiService
import com.example.alfagemefittracker.data.remote.dto.ExerciseDto
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(
    private val workoutDao: WorkoutDao,
    private val workoutLogDao: WorkoutLogDao,
    private val exerciseApiService: ExerciseApiService
) {

    fun getWorkouts(): Flow<List<Workout>> =
        workoutDao.getAllWorkouts()

    suspend fun insertWorkout(workout: Workout) =
        workoutDao.insertWorkout(workout)

    suspend fun updateWorkout(workout: Workout) {
        workoutDao.updateWorkout(workout)
    }

    suspend fun deleteWorkout(workout: Workout) =
        workoutDao.deleteWorkout(workout)

    fun getLogsForWorkout(workoutId: Int): Flow<List<WorkoutLog>> =
        workoutLogDao.getLogsForWorkout(workoutId)

    suspend fun addExerciseToWorkout(workoutId: Int, exercise: ExerciseDto) {
        val log = WorkoutLog(
            workoutId = workoutId,
            exerciseId = exercise.id,
            exerciseName = exercise.name
        )
        workoutLogDao.insertWorkoutLog(log)
    }

    suspend fun updateWorkoutLog(workoutLog: WorkoutLog) {
        workoutLogDao.updateWorkoutLog(workoutLog)
    }

    suspend fun deleteWorkoutLog(workoutLog: WorkoutLog) {
        workoutLogDao.deleteWorkoutLog(workoutLog)
    }

    suspend fun getExercisesFromApi(): List<ExerciseDto> {
        return try {
            // We try to get exercises from the real API
            exerciseApiService.getExercises()
        } catch (e: Exception) {
            // If the API fails (e.g., HTTP 429 Too Many Requests), we return a fake list.
            // This unblocks development and makes the app more resilient.
            createFakeExercises()
        }
    }

    private fun createFakeExercises(): List<ExerciseDto> {
        return listOf(
            ExerciseDto(id = "001", name = "Press de Banca", target = "Pectoral", gifUrl = "", bodyPart = "Pecho", equipment = "Barra"),
            ExerciseDto(id = "002", name = "Sentadilla", target = "Cuádriceps", gifUrl = "", bodyPart = "Piernas", equipment = "Barra"),
            ExerciseDto(id = "003", name = "Dominadas", target = "Espalda", gifUrl = "", bodyPart = "Espalda", equipment = "Peso corporal"),
            ExerciseDto(id = "004", name = "Curl de Bíceps", target = "Bíceps", gifUrl = "", bodyPart = "Brazos", equipment = "Mancuerna")
        )
    }
}
