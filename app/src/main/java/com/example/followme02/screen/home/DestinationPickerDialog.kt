package com.example.followme02.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.followme02.model.Destinations
import com.example.followme02.viewmodel.DestinationViewModel

@Composable
fun DestinationPickerDialog(
    viewModel: DestinationViewModel,
    onDismiss: () -> Unit,
    onDestinationSelected: (Destinations) -> Unit
) {
    val searchText = viewModel.searchText.value
    val filteredDestinations = viewModel.filteredDestinations.value
    val isLoading = viewModel.isLoading.value
    val errorMessage = viewModel.errorMessage.value

    LaunchedEffect(Unit) {
        if (viewModel.allDestinations.value.isEmpty()) {
            viewModel.loadDestinations()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Choose destination")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { viewModel.onSearchTextChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Search destination") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                when {
                    isLoading -> {
                        CircularProgressIndicator()
                    }

                    errorMessage != null -> {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.height(300.dp)
                        ) {
                            items(filteredDestinations) { destination ->
                                DestinationRow(
                                    destination = destination,
                                    onClick = {
                                        onDestinationSelected(destination)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun DestinationRow(
    destination: Destinations,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Text(
            text = destination.name,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "${destination.kmThreshold.toInt()} km from Narvik",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
    }
}