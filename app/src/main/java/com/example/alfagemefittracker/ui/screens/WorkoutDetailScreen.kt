
package com.example.alfagemefittracker.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.alfagemefittracker.data.local.WorkoutLog
import com.example.alfagemefittracker.ui.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    workoutId: Int,
    workoutViewModel: WorkoutViewModel,
    onNavigateBack: () -> Unit
) {
    val workouts by workoutViewModel.workouts.collectAsState()
    val workout = workouts.find { it.id == workoutId }
    val workoutLogs by workoutViewModel.getLogsForWorkout(workoutId).collectAsState(initial = emptyList())

    var showEditWorkoutDialog by remember { mutableStateOf(false) }

    if (showEditWorkoutDialog && workout != null) {
        EditWorkoutDialog(
            workoutName = workout.name,
            onDismiss = { showEditWorkoutDialog = false },
            onConfirm = {
                newName ->
                workoutViewModel.updateWorkout(workout.copy(name = newName))
                showEditWorkoutDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(workout?.name ?: "Detalle del Workout") },
                actions = {
                    IconButton(onClick = { showEditWorkoutDialog = true }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Editar Workout")
                    }
                    IconButton(onClick = {
                        if (workout != null) {
                            workoutViewModel.deleteWorkout(workout)
                            onNavigateBack()
                        }
                    }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar Workout")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                if (workout != null) {
                    Text(text = "Nombre: ${workout.name}", style = MaterialTheme.typography.headlineSmall)
                    Text(text = "Fecha: ${workout.date}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(24.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Ejercicios en esta rutina:", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                } else {
                    Text("Workout no encontrado.")
                }
            }

            if (workoutLogs.isEmpty()) {
                item {
                    Text("Aún no has añadido ejercicios a este entrenamiento.")
                }
            } else {
                items(workoutLogs) { log ->
                    WorkoutLogCard(
                        log = log,
                        onUpdate = workoutViewModel::updateWorkoutLog,
                        onDelete = workoutViewModel::deleteWorkoutLog
                    )
                }
            }
        }
    }
}

@Composable
fun EditWorkoutDialog(
    workoutName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(workoutName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar nombre del Workout") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Nombre") }
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(text) }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun WorkoutLogCard(log: WorkoutLog, onUpdate: (WorkoutLog) -> Unit, onDelete: (WorkoutLog) -> Unit) {
    var sets by remember { mutableStateOf(log.sets.toString()) }
    var reps by remember { mutableStateOf(log.reps.toString()) }
    var weight by remember { mutableStateOf(log.weight.toString()) }
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(text = log.exerciseName, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                IconButton(onClick = { onDelete(log) }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar ejercicio")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = sets,
                    onValueChange = { sets = it },
                    label = { Text("Series") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it },
                    label = { Text("Reps") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Peso (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val updatedLog = log.copy(
                        sets = sets.toIntOrNull() ?: 0,
                        reps = reps.toIntOrNull() ?: 0,
                        weight = weight.toDoubleOrNull() ?: 0.0
                    )
                    onUpdate(updatedLog)
                    Toast.makeText(context, "¡Guardado!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Guardar")
            }
        }
    }
}
