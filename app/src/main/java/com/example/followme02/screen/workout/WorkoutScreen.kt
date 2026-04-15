package com.example.followme02.ui

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.max
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(
    navController: NavController,
    workout: Workout,
    onSave: (Workout) -> Unit,
    onCancel: () -> Unit
) {
    val formatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }
    val today = remember { LocalDate.now() }

    var selectedType by remember { mutableStateOf(workout.exerciseType) }
    var distanceKm by remember { mutableStateOf(workout.distanceKm.coerceAtLeast(0f)) }
    var durationMinutes by remember { mutableStateOf(workout.durationMinutes.coerceAtLeast(0)) }

    val initialDate = remember {
        runCatching { LocalDate.parse(workout.date, formatter) }.getOrElse { today }
    }
    var selectedDate by remember { mutableStateOf(initialDate) }

    var typeExpanded by remember { mutableStateOf(false) }
    var showDateDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Aktivitetstype", style = MaterialTheme.typography.titleMedium)

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
                label = { Text("Velg aktivitet") }
            )

            ExposedDropdownMenu(
                expanded = typeExpanded,
                onDismissRequest = { typeExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Walk") },
                    onClick = { selectedType = ExerciseType.WALK; typeExpanded = false }
                )
                DropdownMenuItem(
                    text = { Text("Run") },
                    onClick = { selectedType = ExerciseType.RUN; typeExpanded = false }
                )
                DropdownMenuItem(
                    text = { Text("Cycle") },
                    onClick = { selectedType = ExerciseType.CYCLE; typeExpanded = false }
                )
                DropdownMenuItem(
                    text = { Text("Ski") },
                    onClick = { selectedType = ExerciseType.SKI; typeExpanded = false }
                )
            }
        }

        Text("Distanse (km)", style = MaterialTheme.typography.titleMedium)

        DistanceStepper(
            value = distanceKm,
            step = 0.1f,
            minValue = 0f,
            onValueChange = { distanceKm = it }
        )

        Text("Tid brukt (minutter)", style = MaterialTheme.typography.titleMedium)

        DurationStepper(
            value = durationMinutes,
            step = 5,
            minValue = 0,
            onValueChange = { durationMinutes = it }
        )

        Text("Dato", style = MaterialTheme.typography.titleMedium)

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
                label = { Text("Velg dato") }
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
                    TextButton(onClick = {
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
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDateDialog = false }) { Text("Avbryt") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Button(onClick = {
                val updated = workout.copy(
                    exerciseType = selectedType,
                    distanceKm = distanceKm,
                    durationMinutes = durationMinutes,
                    date = selectedDate.format(formatter)
                )
                onSave(updated)
            }) {
                Text("Lagre")
            }

            OutlinedButton(
                onClick = {
                    onCancel()
                    navController.navigate("home")
                }
            ) {
                Text("Avbryt")
            }
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
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Øk")
                }
                IconButton(onClick = { onValueChange(max(minValue, value - step)) }) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Minsk")
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
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Øk")
                }
                IconButton(onClick = { onValueChange(max(minValue, value - step)) }) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Minsk")
                }
            }
        }
    }
}