
package com.example.alfagemefittracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alfagemefittracker.data.local.Workout
import com.example.alfagemefittracker.data.local.WorkoutLog
import com.example.alfagemefittracker.data.remote.dto.ExerciseDto
import com.example.alfagemefittracker.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ExerciseUiState {
    object Loading : ExerciseUiState()
    data class Success(val exercises: List<ExerciseDto>) : ExerciseUiState()
    data class Error(val message: String) : ExerciseUiState()
}

class WorkoutViewModel(
    private val repository: WorkoutRepository
) : ViewModel() {

    val workouts: StateFlow<List<Workout>> = repository.getWorkouts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _exerciseState = MutableStateFlow<ExerciseUiState>(ExerciseUiState.Loading)
    val exerciseState: StateFlow<ExerciseUiState> = _exerciseState.asStateFlow()

    private val _selectedExercise = MutableStateFlow<ExerciseDto?>(null)
    val selectedExercise: StateFlow<ExerciseDto?> = _selectedExercise.asStateFlow()

    fun getLogsForWorkout(workoutId: Int): Flow<List<WorkoutLog>> {
        return repository.getLogsForWorkout(workoutId)
    }

    fun addWorkout(name: String, date: String) {
        viewModelScope.launch {
            repository.insertWorkout(
                Workout(name = name, date = date)
            )
        }
    }

    fun updateWorkout(workout: Workout) {
        viewModelScope.launch {
            repository.updateWorkout(workout)
        }
    }

    fun deleteWorkout(workout: Workout) {
        viewModelScope.launch {
            repository.deleteWorkout(workout)
        }
    }

    fun addExerciseToWorkout(workoutId: Int, exercise: ExerciseDto) {
        viewModelScope.launch {
            repository.addExerciseToWorkout(workoutId, exercise)
        }
    }

    fun updateWorkoutLog(workoutLog: WorkoutLog) {
        viewModelScope.launch {
            repository.updateWorkoutLog(workoutLog)
        }
    }

    fun deleteWorkoutLog(workoutLog: WorkoutLog) {
        viewModelScope.launch {
            repository.deleteWorkoutLog(workoutLog)
        }
    }

    fun getExercises() {
        viewModelScope.launch {
            _exerciseState.value = ExerciseUiState.Loading
            try {
                val exercises = repository.getExercisesFromApi()
                _exerciseState.value = ExerciseUiState.Success(exercises)
            } catch (e: Exception) {
                _exerciseState.value = ExerciseUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun selectExercise(exerciseId: String) {
        val currentState = _exerciseState.value
        if (currentState is ExerciseUiState.Success) {
            _selectedExercise.value = currentState.exercises.find { it.id == exerciseId }
        }
    }
}
