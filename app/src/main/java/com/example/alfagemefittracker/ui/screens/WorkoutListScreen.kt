
package com.example.alfagemefittracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.alfagemefittracker.ui.viewmodel.AuthViewModel
import com.example.alfagemefittracker.ui.viewmodel.ExerciseUiState
import com.example.alfagemefittracker.ui.viewmodel.WorkoutViewModel

@Composable
fun WorkoutListScreen(
    workoutViewModel: WorkoutViewModel,
    authViewModel: AuthViewModel,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToAddWorkout: () -> Unit,
    onLogout: () -> Unit
) {
    val workouts by workoutViewModel.workouts.collectAsState(initial = emptyList())
    val exerciseState by workoutViewModel.exerciseState.collectAsState()
    val userProfile by authViewModel.userProfile.collectAsState()

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            userProfile?.name?.let {
                Text("Welcome, $it", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
            }
            Button(onClick = onLogout) {
                Text("Log Out")
            }
        }

        Button(onClick = { onNavigateToAddWorkout() }) {
            Text("Create Workout")
        }

        Button(onClick = { workoutViewModel.getExercises() }) {
            Text("Fetch Exercises")
        }

        when (val state = exerciseState) {
            is ExerciseUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ExerciseUiState.Success -> {
                LazyColumn {
                    items(state.exercises) { exercise ->
                        Text(
                            text = exercise.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToDetail(exercise.id) }
                                .padding(16.dp)
                        )
                    }
                }
            }
            is ExerciseUiState.Error -> {
                Text(text = state.message, modifier = Modifier.padding(16.dp))
            }
        }

        LazyColumn {
            items(workouts) { workout ->
                Text(text = workout.name, modifier = Modifier.padding(16.dp))
            }
        }
    }
}
