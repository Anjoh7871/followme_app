package com.example.followme02.screen.distance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.followme.DistanceError
import com.example.followme.DistanceResult
import com.example.followme.setDistanceKm
import com.example.followme02.R
import com.example.followme02.model.WorkoutDraft

@Composable
fun DistanceLogicScreen(modifier: Modifier = Modifier) {
    var input by rememberSaveable { mutableStateOf("") }
    var draft by remember { mutableStateOf(WorkoutDraft()) }
    var errorText by remember { mutableStateOf<String?>(null) }

    val title = stringResource(R.string.workout_draft_demo_title)
    val labelDistance = stringResource(R.string.label_distance_km)
    val placeholderDistance = stringResource(R.string.placeholder_distance)
    val btnText = stringResource(R.string.btn_save_to_draft)

    val distanceRequiredMsg = stringResource(R.string.error_distance_required)
    val distanceInvalidMsg = stringResource(R.string.error_distance_invalid_number)
    val distanceGtZeroMsg = stringResource(R.string.error_distance_must_be_gt_zero)

    val draftDistancePrefix = stringResource(R.string.draft_distance_prefix)
    val dash = stringResource(R.string.dash)
    val kmSuffix = stringResource(R.string.km_suffix)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = input,
            onValueChange = {
                input = it
                errorText = null
            },
            label = { Text(labelDistance) },
            placeholder = { Text(placeholderDistance) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            isError = errorText != null,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val res = setDistanceKm(draft, input)
                when (res) {
                    is DistanceResult.Success -> {
                        draft = res.updatedDraft
                        errorText = null
                        input = ""
                    }
                    is DistanceResult.Error -> {
                        errorText = when (res.reason) {
                            DistanceError.REQUIRED -> distanceRequiredMsg
                            DistanceError.INVALID_NUMBER -> distanceInvalidMsg
                            DistanceError.MUST_BE_GREATER_THAN_ZERO -> distanceGtZeroMsg
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(btnText)
        }

        if (errorText != null) {
            Text(text = errorText!!, color = MaterialTheme.colorScheme.error)
        }

        val distanceText = draft.distanceKm?.toString() ?: dash
        Text("$draftDistancePrefix $distanceText $kmSuffix")
    }
}