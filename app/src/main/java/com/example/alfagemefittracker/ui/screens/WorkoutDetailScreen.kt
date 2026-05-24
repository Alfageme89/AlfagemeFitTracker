package com.example.alfagemefittracker.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(workout?.name ?: "Rutina", fontWeight = FontWeight.Bold)
                        Text(workout?.date ?: "", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        if (workout != null) {
                            workoutViewModel.deleteWorkout(workout)
                            onNavigateBack()
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = Color.Gray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (workoutLogs.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.AddCircle, 
                                contentDescription = null, 
                                tint = Color.Gray, 
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No hay ejercicios todavía", color = Color.Gray)
                        }
                    }
                }
            } else {
                items(workoutLogs) { log ->
                    ModernWorkoutLogCard(
                        log = log,
                        onUpdate = workoutViewModel::updateWorkoutLog,
                        onDelete = workoutViewModel::deleteWorkoutLog
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernWorkoutLogCard(
    log: WorkoutLog, 
    onUpdate: (WorkoutLog) -> Unit, 
    onDelete: (WorkoutLog) -> Unit
) {
    var sets by remember { mutableIntStateOf(log.sets) }
    var reps by remember { mutableIntStateOf(log.reps) }
    var weight by remember { mutableStateOf(log.weight.toString()) }
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = log.exerciseName.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.weight(1f),
                    letterSpacing = 1.sp
                )
                IconButton(onClick = { onDelete(log) }) {
                    Icon(
                        imageVector = Icons.Default.Close, 
                        contentDescription = null, 
                        modifier = Modifier.size(18.dp), 
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Selector de SERIES
                CounterInput(
                    label = "SERIES",
                    value = sets,
                    onValueChange = { sets = it }
                )

                // Selector de REPS
                CounterInput(
                    label = "REPS",
                    value = reps,
                    onValueChange = { reps = it }
                )

                // Input de PESO
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("PESO (KG)", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.width(80.dp),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val updatedLog = log.copy(
                        sets = sets,
                        reps = reps,
                        weight = weight.toDoubleOrNull() ?: 0.0
                    )
                    onUpdate(updatedLog)
                    Toast.makeText(context, "Progreso guardado", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary, 
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Check, 
                    contentDescription = null, 
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("GUARDAR PROGRESO", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CounterInput(label: String, value: Int, onValueChange: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            IconButton(
                onClick = { if (value > 0) onValueChange(value - 1) },
                modifier = Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text("-", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
            }
            
            Text(
                text = value.toString(),
                modifier = Modifier.padding(horizontal = 12.dp),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = { onValueChange(value + 1) },
                modifier = Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.Default.Add, 
                    contentDescription = null, 
                    modifier = Modifier.size(16.dp), 
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
