package com.example.followme02.screen.workout

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.followme02.model.ExerciseType
import com.example.followme02.model.Workout
import com.example.followme02.viewmodel.WorkoutViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.max
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen2(
    navController: NavController,
    workout: Workout,
    onCancel: () -> Unit,
    viewModel: WorkoutViewModel
) {
    val formatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }
    val today = remember { LocalDate.now() }

    val workoutList = viewModel.workouts.value
    val isLoading = viewModel.isLoading.value
    val vmMessage = viewModel.message.value

    LaunchedEffect(Unit) {
        viewModel.loadWorkouts()
    }

    var selectedWorkoutId by remember { mutableStateOf<Int?>(null) }
    var localMessage by remember { mutableStateOf("") }

    var selectedType by remember { mutableStateOf(workout.exerciseType) }
    var distanceKm by remember { mutableStateOf(workout.distanceKm.coerceAtLeast(0f)) }
    var durationMinutes by remember { mutableStateOf(workout.durationMinutes.coerceAtLeast(0)) }

    val initialDate = remember {
        runCatching { LocalDate.parse(workout.date, formatter) }.getOrElse { today }
    }
    var selectedDate by remember { mutableStateOf(initialDate) }

    var typeExpanded by remember { mutableStateOf(false) }
    var showDateDialog by remember { mutableStateOf(false) }

    fun loadWorkoutIntoForm(item: Workout) {
        selectedWorkoutId = item.id
        selectedType = item.exerciseType
        distanceKm = item.distanceKm
        durationMinutes = item.durationMinutes
        selectedDate = runCatching {
            LocalDate.parse(item.date, formatter)
        }.getOrElse { today }
        localMessage = "Økta ble valgt i lista."
    }

    fun clearForm() {
        selectedWorkoutId = null
        selectedType = ExerciseType.RUN
        distanceKm = 0f
        durationMinutes = 0
        selectedDate = today
        localMessage = "Skjema tømt."
    }

    fun addWorkout() {
        if (distanceKm <= 0f) {
            localMessage = "Distanse må være større enn 0."
            return
        }

        if (durationMinutes <= 0) {
            localMessage = "Tid brukt må være større enn 0 minutter."
            return
        }

        val newWorkout = Workout(
            id = 0,
            exerciseType = selectedType,
            distanceKm = distanceKm,
            durationMinutes = durationMinutes,
            date = selectedDate.format(formatter)
        )

        viewModel.addWorkout(newWorkout)
        clearForm()
    }

    fun updateWorkout() {
        localMessage = "Update er ikke koblet til database enda."
    }

    fun deleteWorkout() {
        localMessage = "Delete er ikke koblet til database enda."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Training Sessions", style = MaterialTheme.typography.headlineMedium)

        Text(
            "Her kan brukeren registrere treningsøkter og se tidligere økter.",
            style = MaterialTheme.typography.bodyMedium
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Existing sessions", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.width(8.dp))

                when {
                    isLoading && workoutList.isEmpty() -> {
                        CircularProgressIndicator()
                    }

                    workoutList.isEmpty() -> {
                        Text("No sessions registered yet.")
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 220.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(workoutList) { item ->
                                SessionItem(
                                    workout = item,
                                    isSelected = item.id == selectedWorkoutId,
                                    onClick = { loadWorkoutIntoForm(item) }
                                )
                            }
                        }
                    }
                }
            }
        }

        Text("Activity type", style = MaterialTheme.typography.titleMedium)

        ExposedDropdownMenuBox(
            expanded = typeExpanded,
            onExpandedChange = { typeExpanded = !typeExpanded }
        ) {
            OutlinedTextField(
                value = selectedType.name,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                label = { Text("Choose activity") }
            )

            ExposedDropdownMenu(
                expanded = typeExpanded,
                onDismissRequest = { typeExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Walk") },
                    onClick = {
                        selectedType = ExerciseType.WALK
                        typeExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Run") },
                    onClick = {
                        selectedType = ExerciseType.RUN
                        typeExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Cycle") },
                    onClick = {
                        selectedType = ExerciseType.CYCLE
                        typeExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Ski") },
                    onClick = {
                        selectedType = ExerciseType.SKI
                        typeExpanded = false
                    }
                )
            }
        }

        Text("Distance (km)", style = MaterialTheme.typography.titleMedium)

        DistanceStepper(
            value = distanceKm,
            step = 0.1f,
            minValue = 0f,
            onValueChange = { distanceKm = it }
        )

        Text("Duration (minutes)", style = MaterialTheme.typography.titleMedium)

        DurationStepper(
            value = durationMinutes,
            step = 5,
            minValue = 0,
            onValueChange = { durationMinutes = it }
        )

        Text("Date", style = MaterialTheme.typography.titleMedium)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDateDialog = true }
        ) {
            OutlinedTextField(
                value = selectedDate.format(formatter),
                onValueChange = {},
                enabled = false,
                leadingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Choose date") }
            )
        }

        if (showDateDialog) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = selectedDate
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli(),
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        val picked = Instant.ofEpochMilli(utcTimeMillis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        return !picked.isAfter(today)
                    }
                }
            )

            DatePickerDialog(
                onDismissRequest = { showDateDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val millis = datePickerState.selectedDateMillis
                            if (millis != null) {
                                val picked = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                if (!picked.isAfter(today)) {
                                    selectedDate = picked
                                }
                            }
                            showDateDialog = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDateDialog = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { addWorkout() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Add")
            }

            Button(
                onClick = { updateWorkout() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Update")
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = { deleteWorkout() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Delete")
            }

            OutlinedButton(
                onClick = { clearForm() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Clear")
            }
        }

        OutlinedButton(
            onClick = {
                onCancel()
                navController.navigate("home")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Home")
        }

        if (vmMessage != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = vmMessage,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else if (localMessage.isNotBlank()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = localMessage,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun SessionItem(
    workout: Workout,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = if (isSelected) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Type: ${workout.exerciseType}")
            Text("Distance: ${workout.distanceKm} km")
            Text("Duration: ${workout.durationMinutes} min")
            Text("Date: ${workout.date}")
        }
    }
}

@Composable
private fun DistanceStepper(
    value: Float,
    step: Float,
    minValue: Float,
    onValueChange: (Float) -> Unit
) {
    val shown = ((value * 10).roundToInt() / 10f)
    var inputText by remember(value) { mutableStateOf(shown.toString()) }

    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { newValue ->
                    inputText = newValue
                    val parsed = newValue.replace(',', '.').toFloatOrNull()
                    if (parsed != null) {
                        onValueChange(max(minValue, parsed))
                    }
                },
                label = { Text("km") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(12.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = { onValueChange(value + step) }) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase")
                }
                IconButton(onClick = { onValueChange(max(minValue, value - step)) }) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrease")
                }
            }
        }
    }
}

@Composable
private fun DurationStepper(
    value: Int,
    step: Int,
    minValue: Int,
    onValueChange: (Int) -> Unit
) {
    var inputText by remember(value) { mutableStateOf(value.toString()) }

    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { newValue ->
                    inputText = newValue.filter { it.isDigit() }
                    val parsed = inputText.toIntOrNull()
                    if (parsed != null) {
                        onValueChange(max(minValue, parsed))
                    }
                },
                label = { Text("minutes") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(12.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = { onValueChange(value + step) }) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase")
                }
                IconButton(onClick = { onValueChange(max(minValue, value - step)) }) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrease")
                }
            }
        }
    }
}