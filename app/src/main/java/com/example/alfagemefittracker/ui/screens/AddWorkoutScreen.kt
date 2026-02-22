
package com.example.alfagemefittracker.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.alfagemefittracker.ui.viewmodel.WorkoutViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddWorkoutScreen(
    viewModel: WorkoutViewModel,
    onWorkoutSaved: () -> Unit
) {
    var workoutName by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Scaffold {
        innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("Create New Workout", style = MaterialTheme.typography.headlineSmall)

            OutlinedTextField(
                value = workoutName,
                onValueChange = {
                    workoutName = it
                    if (it.isNotBlank()) {
                        isError = false
                    }
                },
                label = { Text("Workout Name") },
                isError = isError,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                singleLine = true
            )

            if (isError) {
                Text(
                    text = "Workout name cannot be empty.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Button(
                onClick = {
                    if (workoutName.isNotBlank()) {
                        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        viewModel.addWorkout(workoutName, currentDate)
                        onWorkoutSaved() // Navegamos hacia atrás
                    } else {
                        isError = true
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
            ) {
                Text("Save Workout")
            }
        }
    }
}
