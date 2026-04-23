package com.example.followme02.screen.journey

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.followme02.R
import com.example.followme02.model.JourneyUiModel
import com.example.followme02.viewmodel.DestinationViewModel
import com.example.followme02.viewmodel.JourneyViewModel
import com.example.followme02.viewmodel.ProfileViewModel
import com.example.followme02.viewmodel.SortType

private fun imageUrlToDrawableName(imageUrl: String?): String? {
    if (imageUrl.isNullOrBlank()) return null

    return imageUrl
        .substringAfterLast("/")
        .substringBeforeLast(".")
        .lowercase()
}

@Composable
fun JourneyLogScreen(
    journeyViewModel: JourneyViewModel = viewModel(),
    destinationViewModel: DestinationViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel()
) {
    val journeys by journeyViewModel.journeys
    val sortType by journeyViewModel.sortType
    val isLoading by journeyViewModel.isLoading

    val selectedDestination = destinationViewModel.selectedDestination.value
    val profile = profileViewModel.uiState.value

    var selectedCompletedJourney by remember { mutableStateOf<JourneyUiModel?>(null) }

    LaunchedEffect(Unit) {
        journeyViewModel.loadJourneys()
        destinationViewModel.loadDestinations()
        profileViewModel.loadUser()
    }

    LaunchedEffect(profile.totalAccumulatedKm, selectedDestination?.destinationId) {
        destinationViewModel.syncCurrentJourney(profile.totalAccumulatedKm)
    }

    val colorScheme = MaterialTheme.colorScheme

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            colorScheme.background,
            colorScheme.surface,
            colorScheme.surfaceContainerLow
        )
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colorScheme.primary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Column {
                            Text(
                                text = stringResource(R.string.journey_log),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = colorScheme.onBackground
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = stringResource(R.string.track_your_active_route_and_review_completed_virtual_journeys),
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    item {
                        CurrentJourneyCard(
                            destinationName = selectedDestination?.name,
                            currentKm = destinationViewModel.currentJourneyKm.value,
                            targetKm = selectedDestination?.kmThreshold
                        )
                    }

                    item {
                        Text(
                            text = stringResource(R.string.completed_journeys),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onBackground
                        )
                    }

                    item {
                        SortSection(
                            selectedSort = sortType,
                            onSortSelected = { journeyViewModel.changeSort(it) }
                        )
                    }

                    if (journeys.isEmpty()) {
                        item {
                            EmptyJourneyHistoryCard()
                        }
                    } else {
                        items(journeys) { journey ->
                            JourneyHistoryCard(
                                journey = journey,
                                onClick = {
                                    selectedCompletedJourney = journey
                                }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    selectedCompletedJourney?.let { journey ->
        CompletedJourneyDialog(
            journey = journey,
            onDismiss = { selectedCompletedJourney = null }
        )
    }
}

@Composable
fun CurrentJourneyCard(
    destinationName: String?,
    currentKm: Double,
    targetKm: Double?
) {
    val colorScheme = MaterialTheme.colorScheme

    val safeTargetKm = targetKm ?: 0.0
    val progress = if (safeTargetKm > 0) {
        (currentKm / safeTargetKm).coerceIn(0.0, 1.0).toFloat()
    } else {
        0f
    }

    val remainingKm = if (safeTargetKm > 0) {
        (safeTargetKm - currentKm).coerceAtLeast(0.0)
    } else {
        0.0
    }

    val currentKmText = if (currentKm % 1.0 == 0.0) {
        currentKm.toInt().toString()
    } else {
        String.format("%.1f", currentKm)
    }

    val targetKmText = if (safeTargetKm % 1.0 == 0.0) {
        safeTargetKm.toInt().toString()
    } else {
        String.format("%.1f", safeTargetKm)
    }

    val remainingKmText = if (remainingKm % 1.0 == 0.0) {
        remainingKm.toInt().toString()
    } else {
        String.format("%.1f", remainingKm)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
        ) {
            Text(
                text = stringResource(R.string.current_journey),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (destinationName == null || safeTargetKm <= 0.0) {
                Text(
                    text =stringResource(R.string.no_destination),
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorScheme.onPrimaryContainer.copy(alpha = 0.88f)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.go_to_home_and_choose_a_destination_in_virtual_journey),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onPrimaryContainer.copy(alpha = 0.74f)
                )
            } else {
                Text(
                    text = destinationName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "$currentKmText / $targetKmText km",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(50)),
                    color = colorScheme.primary,
                    trackColor = colorScheme.onPrimaryContainer.copy(alpha = 0.10f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = stringResource(
                        R.string.km_left_to_reach,
                        remainingKmText,
                        destinationName
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onPrimaryContainer.copy(alpha = 0.82f)
                )
            }
        }
    }
}

@Composable
fun SortSection(
    selectedSort: SortType,
    onSortSelected: (SortType) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.sort_completed_journeys),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SortChip(
                    label = stringResource(R.string.date),
                    icon = Icons.Default.CalendarToday,
                    selected = selectedSort == SortType.DATE,
                    onClick = { onSortSelected(SortType.DATE) }
                )

                SortChip(
                    label = stringResource(R.string.distance),
                    icon = Icons.Default.Route,
                    selected = selectedSort == SortType.KM,
                    onClick = { onSortSelected(SortType.KM) }
                )

                SortChip(
                    label = stringResource(R.string.name),
                    icon = Icons.Default.SortByAlpha,
                    selected = selectedSort == SortType.NAME,
                    onClick = { onSortSelected(SortType.NAME) }
                )
            }
        }
    }
}

@Composable
fun SortChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    AssistChip(
        onClick = onClick,
        label = {
            Text(
                text = label,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.width(18.dp)
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) {
                colorScheme.secondaryContainer
            } else {
                colorScheme.surface
            },
            labelColor = if (selected) {
                colorScheme.onSecondaryContainer
            } else {
                colorScheme.onSurfaceVariant
            },
            leadingIconContentColor = if (selected) {
                colorScheme.onSecondaryContainer
            } else {
                colorScheme.onSurfaceVariant
            }
        ),
        border = AssistChipDefaults.assistChipBorder(
            enabled = true,
            borderColor = if (selected) {
                colorScheme.secondary.copy(alpha = 0.45f)
            } else {
                colorScheme.outlineVariant.copy(alpha = 0.5f)
            }
        )
    )
}

@Composable
fun JourneyHistoryCard(
    journey: JourneyUiModel,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(colorScheme.secondaryContainer)
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Flag,
                    contentDescription = null,
                    tint = colorScheme.onSecondaryContainer
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = journey.destinationName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${journey.km.toInt()} km",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = colorScheme.outlineVariant)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.completed, formatJourneyDate(journey.completedAt)),
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CompletedJourneyDialog(
    journey: JourneyUiModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val drawableName = imageUrlToDrawableName(journey.imageUrl)

    val imageResId = remember(drawableName) {
        drawableName?.let {
            context.resources.getIdentifier(it, "drawable", context.packageName)
        } ?: 0
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        },
        title = {
            Text(
                text = journey.destinationName,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (imageResId != 0) {
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = journey.destinationName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                Text(
                    text = journey.factText ?: stringResource(R.string.no_fun_fact),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurface
                )
            }
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = true
        )
    )
}

@Composable
fun EmptyJourneyHistoryCard() {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.no_completed_journeys),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.finish_your_first_virtual_route_and_it_will_show_up_here),
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

fun formatJourneyDate(rawDate: String): String {
    return rawDate
        .replace("T", " ")
        .replace("Z", "")
        .take(16)
}