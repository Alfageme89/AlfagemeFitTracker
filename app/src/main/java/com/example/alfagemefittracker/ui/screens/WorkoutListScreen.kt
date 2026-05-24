
package com.example.alfagemefittracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onNavigateToSettings: () -> Unit,
) {
    val workouts by workoutViewModel.workouts.collectAsState()
    val exerciseState by workoutViewModel.exerciseState.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todos") }
    
    var showDialog by remember { mutableStateOf(false) }
    var selectedExercise by remember { mutableStateOf<ExerciseDto?>(null) }

    val categories = listOf("Todos", "Pecho", "Piernas", "Espalda", "Brazos", "Hombros", "Abdomen")

    LaunchedEffect(Unit) {
        workoutViewModel.getExercises()
    }

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
            CenterAlignedTopAppBar(
                title = { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("ALFAGEME", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        Text("FIT TRACKER", fontWeight = FontWeight.Black, fontSize = 18.sp)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Ajustes")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddWorkout,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nuevo")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                SectionHeader(title = "MIS RUTINAS", icon = Icons.Default.List)
            }

            if (workouts.isEmpty()) {
                item { EmptyWorkoutsState() }
            } else {
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        items(workouts) { workout ->
                            WorkoutMiniCard(workout = workout, onClick = { onNavigateToWorkoutDetail(workout.id) })
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(title = "EXPLORAR EJERCICIOS", icon = Icons.Default.Search)
                
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }

            when (val state = exerciseState) {
                is ExerciseUiState.Loading -> {
                    item { LoadingBox() }
                }
                is ExerciseUiState.Success -> {
                    val filteredExercises = state.exercises.filter { 
                        (selectedCategory == "Todos" || it.bodyPart.contains(selectedCategory, ignoreCase = true)) &&
                        it.name.contains(searchQuery, ignoreCase = true)
                    }
                    
                    items(filteredExercises) { exercise ->
                        ModernExerciseCard(
                            exercise = exercise, 
                            onClick = {
                                selectedExercise = exercise
                                showDialog = true
                            }
                        )
                    }
                }
                is ExerciseUiState.Error -> {
                    item { ErrorText(state.message) }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, icon: ImageVector) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
    }
}

@Composable
fun WorkoutMiniCard(workout: Workout, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(160.dp, 100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(16.dp).align(Alignment.BottomStart)) {
                Text(
                    text = workout.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = workout.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun ModernExerciseCard(exercise: ExerciseDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when(exercise.bodyPart.lowercase()) {
                        "pecho" -> Icons.Default.Favorite
                        "piernas" -> Icons.Default.Star
                        "espalda" -> Icons.Default.Person
                        else -> Icons.Default.FlashOn
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = exercise.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "${exercise.bodyPart} • ${exercise.equipment}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            Icon(imageVector = Icons.Default.AddCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun EmptyWorkoutsState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        Text("No hay rutinas guardadas", color = Color.Gray)
    }
}

@Composable
fun LoadingBox() {
    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun ErrorText(message: String) {
    Text(
        text = "Error: $message",
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        textAlign = TextAlign.Center
    )
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
        title = { Text("Añadir a mi rutina", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Selecciona el entrenamiento para:\n\"$exerciseName\"", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                if (workouts.isEmpty()) {
                    Text("No tienes rutinas creadas todavía.", color = Color.Gray)
                } else {
                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                        items(workouts) { workout ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onWorkoutSelected(workout) }
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ) {
                                Text(
                                    text = workout.name,
                                    modifier = Modifier.padding(16.dp),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCELAR", color = Color.Gray) }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}
