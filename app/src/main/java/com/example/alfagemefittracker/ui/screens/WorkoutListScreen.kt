
package com.example.alfagemefittracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.alfagemefittracker.data.local.Workout
import com.example.alfagemefittracker.data.remote.dto.ExerciseDto
import com.example.alfagemefittracker.ui.viewmodel.ExerciseUiState
import com.example.alfagemefittracker.ui.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutListScreen(
    workoutViewModel: WorkoutViewModel,
    onNavigateToWorkoutDetail: (Int) -> Unit,
    onNavigateToExerciseDetail: (String) -> Unit,
    onNavigateToAddWorkout: () -> Unit,
) {
    val workouts by workoutViewModel.workouts.collectAsState(initial = emptyList())
    val exerciseState by workoutViewModel.exerciseState.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var selectedExercise by remember { mutableStateOf<ExerciseDto?>(null) }

    if (showDialog && selectedExercise != null) {
        AddExerciseToWorkoutDialog(
            exerciseName = selectedExercise?.name ?: "",
            workouts = workouts,
            onDismiss = { showDialog = false },
            onWorkoutSelected = { workout ->
                selectedExercise?.let { exercise ->
                    workoutViewModel.addExerciseToWorkout(workout.id, exercise)
                }
                showDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FitTrack") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddWorkout) {
                Icon(Icons.Filled.Add, contentDescription = "Crear entrenamiento")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Mis Entrenamientos", style = MaterialTheme.typography.headlineMedium)
            }

            if (workouts.isEmpty()) {
                item {
                    EmptyState()
                }
            } else {
                items(workouts) { workout ->
                    WorkoutCard(workout = workout, onClick = { onNavigateToWorkoutDetail(workout.id) })
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Explorar Ejercicios", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { workoutViewModel.getExercises() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Cargar ejercicios de la API")
                }
            }

            when (val state = exerciseState) {
                is ExerciseUiState.Loading -> {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
                is ExerciseUiState.Success -> {
                    items(state.exercises) { exercise ->
                        ExerciseCard(exercise = exercise, onClick = {
                            selectedExercise = exercise
                            showDialog = true
                        })
                    }
                }
                is ExerciseUiState.Error -> {
                    item {
                        Text(
                            text = "Error: ${state.message}",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddExerciseToWorkoutDialog(
    exerciseName: String,
    workouts: List<Workout>,
    onDismiss: () -> Unit,
    onWorkoutSelected: (Workout) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir Ejercicio") },
        text = {
            Column {
                Text("¿A qué entrenamiento quieres añadir \"$exerciseName\"?")
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn {
                    items(workouts) { workout ->
                        Text(
                            text = workout.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onWorkoutSelected(workout) }
                                .padding(vertical = 12.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}


@Composable
fun WorkoutCard(workout: Workout, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = workout.name, style = MaterialTheme.typography.titleLarge)
            Text(text = "Fecha: ${workout.date}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ExerciseCard(exercise: ExerciseDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = exercise.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "Parte del cuerpo: ${exercise.bodyPart}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth().padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = """Aún no tienes entrenamientos.
¡Crea uno nuevo con el botón +!""",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}
