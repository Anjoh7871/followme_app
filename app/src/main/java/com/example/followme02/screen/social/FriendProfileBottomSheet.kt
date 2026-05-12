package com.example.followme02.screen.social

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.followme02.R
import com.example.followme02.model.JourneyUiModel
import com.example.followme02.screen.profile.FriendCurrentJourneyUi
import com.example.followme02.screen.profile.ProfileAvatar
import com.example.followme02.viewmodel.FriendProfileViewModel

data class FriendProfileSheetPreview(
    val userId: Int,
    val username: String,
    val email: String,
    val avatarUrl: String?,
    val level: Int,
    val totalPoints: Int,
    val totalKm: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendProfileBottomSheet(
    preview: FriendProfileSheetPreview,
    showRemoveFriendButton: Boolean,
    onDismiss: () -> Unit,
    onFriendRemoved: () -> Unit = {},
    viewModel: FriendProfileViewModel = viewModel(
        key = "friend-profile-sheet-${preview.userId}"
    )
) {
    val state = viewModel.uiState.value
    val colorScheme = MaterialTheme.colorScheme
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val sheetMaxHeight = LocalConfiguration.current.screenHeightDp.dp * 0.88f

    var showRemoveDialog by remember { mutableStateOf(false) }

    LaunchedEffect(preview.userId) {
        viewModel.loadFriendProfile(preview.userId)
    }

    LaunchedEffect(state.friendRemoved) {
        if (state.friendRemoved) {
            onFriendRemoved()
            onDismiss()
        }
    }

    val friend = state.friend

    val displayPreview = if (friend != null) {
        FriendProfileSheetPreview(
            userId = friend.userId,
            username = friend.username,
            email = friend.email,
            avatarUrl = friend.avatarUrl,
            level = friend.level,
            totalPoints = friend.totalPoints,
            totalKm = friend.totalKm
        )
    } else {
        preview
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = sheetMaxHeight)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                BottomSheetHeader(
                    title = stringResource(R.string.friend_profile),
                    onClose = onDismiss
                )
            }

            item {
                FriendProfileSheetMiniCard(friend = displayPreview)
            }

            when {
                state.isLoading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = colorScheme.primary)
                        }
                    }
                }

                state.errorMessage != null -> {
                    item {
                        EmptyStateCard(
                            title = stringResource(R.string.friend_not_found),
                            description = state.errorMessage
                        )
                    }
                }

                friend != null -> {
                    item {
                        FriendProfileStatsRow(
                            level = friend.level,
                            achievementsCount = state.achievementsCount,
                            totalKm = friend.totalKm
                        )
                    }

                    item {
                        FriendProfileCurrentJourneyCard(
                            journey = state.currentJourney
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

                    if (state.completedJourneys.isEmpty()) {
                        item {
                            EmptyStateCard(
                                title = stringResource(R.string.completed_journeys),
                                description = stringResource(R.string.no_completed_journeys)
                            )
                        }
                    } else {
                        items(state.completedJourneys) { journey ->
                            FriendProfileCompletedJourneyCard(journey = journey)
                        }
                    }

                    if (showRemoveFriendButton) {
                        item {
                            Button(
                                onClick = { showRemoveDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorScheme.error,
                                    contentColor = colorScheme.onError
                                ),
                                shape = RoundedCornerShape(18.dp)
                            ) {
                                Text(stringResource(R.string.remove_friend))
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    if (showRemoveDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveDialog = false },
            title = {
                Text(stringResource(R.string.remove_friend))
            },
            text = {
                Text(
                    stringResource(
                        R.string.remove_friend_confirm,
                        displayPreview.username
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRemoveDialog = false
                        viewModel.removeFriend(displayPreview.userId)
                    }
                ) {
                    Text(
                        text = stringResource(R.string.remove),
                        color = colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showRemoveDialog = false }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun FriendProfileSheetMiniCard(
    friend: FriendProfileSheetPreview
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) {
                colorScheme.surfaceContainer
            } else {
                colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDark) 2.dp else 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProfileAvatar(
                    username = friend.username,
                    avatarUrl = friend.avatarUrl,
                    modifier = Modifier.size(74.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = friend.username,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = friend.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(
                            R.string.friend_profile_level_value,
                            friend.level
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FriendProfileSmallStatCard(
                    title = stringResource(R.string.points),
                    value = friend.totalPoints.toString(),
                    modifier = Modifier.weight(1f)
                )

                FriendProfileSmallStatCard(
                    title = stringResource(R.string.total_km),
                    value = formatKm(friend.totalKm),
                    modifier = Modifier.weight(1f)
                )

                FriendProfileSmallStatCard(
                    title = stringResource(R.string.level),
                    value = friend.level.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun FriendProfileSmallStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) {
                colorScheme.surfaceContainerHigh
            } else {
                colorScheme.surfaceVariant.copy(alpha = 0.45f)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FriendProfileStatsRow(
    level: Int,
    achievementsCount: Int,
    totalKm: Double
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        FriendProfileLargeStatCard(
            title = stringResource(R.string.level),
            value = level.toString(),
            icon = Icons.Default.Flag,
            modifier = Modifier.weight(1f)
        )

        FriendProfileLargeStatCard(
            title = stringResource(R.string.achievements),
            value = achievementsCount.toString(),
            icon = Icons.Default.EmojiEvents,
            modifier = Modifier.weight(1f)
        )

        FriendProfileLargeStatCard(
            title = stringResource(R.string.total_km),
            value = formatKm(totalKm),
            icon = Icons.Default.Straighten,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun FriendProfileLargeStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) {
                colorScheme.surfaceContainer
            } else {
                colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FriendProfileCurrentJourneyCard(
    journey: FriendCurrentJourneyUi
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) {
                colorScheme.primaryContainer.copy(alpha = 0.55f)
            } else {
                colorScheme.primaryContainer
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDark) 2.dp else 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = null,
                    tint = colorScheme.primary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(R.string.current_journey),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = journey.destinationName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (journey.hasActiveJourney) {
                LinearProgressIndicator(
                    progress = { journey.progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(50)),
                    color = colorScheme.primary,
                    trackColor = colorScheme.onPrimaryContainer.copy(alpha = 0.12f)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(
                            R.string.journey_progress,
                            (journey.progressFraction * 100).toInt()
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )

                    Text(
                        text = stringResource(
                            R.string.journey_km_progress,
                            formatKm(journey.progressKm),
                            formatKm(journey.targetKm)
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onPrimaryContainer.copy(alpha = 0.82f)
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.no_active_journey),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onPrimaryContainer.copy(alpha = 0.82f)
                )
            }
        }
    }
}

@Composable
private fun FriendProfileCompletedJourneyCard(
    journey: JourneyUiModel
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) {
                colorScheme.surfaceContainer
            } else {
                colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Route,
                contentDescription = null,
                tint = colorScheme.primary
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = journey.destinationName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${formatKm(journey.km)} ${stringResource(R.string.km)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.done),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary
            )
        }
    }
}

private fun formatKm(km: Double): String {
    return if (km % 1.0 == 0.0) {
        km.toInt().toString()
    } else {
        "%.1f".format(km)
    }
}