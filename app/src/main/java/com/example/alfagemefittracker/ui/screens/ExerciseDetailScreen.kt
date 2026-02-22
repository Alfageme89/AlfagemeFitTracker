package com.example.alfagemefittracker.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.alfagemefittracker.ui.viewmodel.WorkoutViewModel

@Composable
fun ExerciseDetailScreen(viewModel: WorkoutViewModel, exerciseId: String?) {
    // Le decimos al ViewModel que cargue los detalles de este ejercicio específico.
    // LaunchedEffect se asegura de que esto se llame solo una vez cuando el ID cambie.
    LaunchedEffect(exerciseId) {
        if (exerciseId != null) {
            viewModel.selectExercise(exerciseId)
        }
    }

    val exercise by viewModel.selectedExercise.collectAsState()

    Scaffold { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (exercise == null) {
                Text(text = "Loading exercise details...")
            } else {
                Text(text = "Name: ${exercise!!.name}")
                Text(text = "Target: ${exercise!!.target}")
                Text(text = "Body Part: ${exercise!!.bodyPart}")
                Text(text = "Equipment: ${exercise!!.equipment}")
            }
        }
    }
}
