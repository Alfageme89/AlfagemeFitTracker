
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
            val apiExercises = exerciseApiService.getExercises()
            if (apiExercises.isEmpty()) createFakeExercises() else apiExercises
        } catch (e: Exception) {
            createFakeExercises()
        }
    }

    private fun createFakeExercises(): List<ExerciseDto> {
        return listOf(
            ExerciseDto("001", "Press de Banca", "Pectoral", "", "Pecho", "Barra"),
            ExerciseDto("002", "Sentadilla", "Cuádriceps", "", "Piernas", "Barra"),
            ExerciseDto("003", "Dominadas", "Espalda", "", "Espalda", "Peso corporal"),
            ExerciseDto("004", "Curl de Bíceps", "Bíceps", "", "Brazos", "Mancuerna"),
            ExerciseDto("005", "Press Militar", "Deltoides", "", "Hombros", "Barra"),
            ExerciseDto("006", "Peso Muerto", "Isquios", "", "Espalda/Piernas", "Barra"),
            ExerciseDto("007", "Zancadas", "Glúteos", "", "Piernas", "Mancuerna"),
            ExerciseDto("008", "Fondos de Tríceps", "Tríceps", "", "Brazos", "Peso corporal"),
            ExerciseDto("009", "Remo con Barra", "Dorsal", "", "Espalda", "Barra"),
            ExerciseDto("010", "Plancha Abdominal", "Core", "", "Abdomen", "Peso corporal"),
            ExerciseDto("011", "Elevaciones Laterales", "Deltoides", "", "Hombros", "Mancuerna"),
            ExerciseDto("012", "Prensa de Piernas", "Cuádriceps", "", "Piernas", "Máquina"),
            ExerciseDto("013", "Extensiones de Cuádriceps", "Cuádriceps", "", "Piernas", "Máquina"),
            ExerciseDto("014", "Press Francés", "Tríceps", "", "Brazos", "Barra EZ"),
            ExerciseDto("015", "Martillo Bíceps", "Bíceps", "", "Brazos", "Mancuerna"),
            ExerciseDto("016", "Jalón al Pecho", "Espalda", "", "Espalda", "Polea"),
            ExerciseDto("017", "Aperturas con Mancuerna", "Pecho", "", "Pecho", "Mancuerna"),
            ExerciseDto("018", "Puente de Glúteo", "Glúteo", "", "Piernas", "Peso corporal"),
            ExerciseDto("019", "Flexiones", "Pecho", "", "Pecho", "Peso corporal"),
            ExerciseDto("020", "Burpees", "Cardio", "", "Cuerpo entero", "Peso corporal")
        )
    }
}
