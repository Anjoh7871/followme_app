package com.example.followme02.screen.profile

import androidx.compose.ui.graphics.Brush
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.followme02.R
import com.example.followme02.viewmodel.ProfileViewModel
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon

@Composable
fun ProfileScreen(
    navController: NavController,
    isDarkMode: Boolean,
    viewModel: ProfileViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val profile = viewModel.uiState.value
    val isLoading = viewModel.isLoading.value
    val error = viewModel.errorMessage.value
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        viewModel.loadUser()
    }

    val totalKmText = if (profile.totalAccumulatedKm % 1.0 == 0.0) {
        "${profile.totalAccumulatedKm.toInt()} km"
    } else {
        "${profile.totalAccumulatedKm} km"
    }

    val screenBackground = colorScheme.background

    val mainCardColor = if (isDarkMode) {
        colorScheme.surfaceContainer
    } else {
        colorScheme.surface
    }

    val secondaryCardColor = if (isDarkMode) {
        colorScheme.surfaceContainerHigh
    } else {
        colorScheme.surface
    }

    val detailsCardColor = if (isDarkMode) {
        colorScheme.surfaceContainer
    } else {
        colorScheme.surface
    }

    val featuredJourneyTitleColor = if (isDarkMode) {
        colorScheme.onPrimaryContainer
    } else {
        colorScheme.onPrimaryContainer.copy(alpha = 0.82f)
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = screenBackground
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(screenBackground)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = colorScheme.primary)
                    }
                }

                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = error,
                            color = colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        mainCardColor,
                                        colorScheme.primaryContainer.copy(alpha = if (isDarkMode) 0.10f else 0.22f)
                                    )
                                )
                            )
                            .padding(22.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.profile_title),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.profile_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = colorScheme.outlineVariant.copy(
                                        alpha = if (isDarkMode) 0.28f else 0.65f
                                    ),
                                    shape = RoundedCornerShape(32.dp)
                                ),
                            shape = RoundedCornerShape(32.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = mainCardColor
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = if (isDarkMode) 2.dp else 6.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(22.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ProfileAvatar(
                                        username = profile.username,
                                        avatarUrl = profile.avatarUrl
                                    )

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = profile.username,
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = colorScheme.onSurface
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = profile.email.ifBlank { "Logged in user" },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = colorScheme.onSurfaceVariant
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = stringResource(R.string.profile_motivation),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = colorScheme.onSurfaceVariant.copy(alpha = 0.82f)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    StatBadge(
                                        title = stringResource(R.string.level),
                                        value = profile.currentLevel.toString(),
                                        backgroundColor = if (isDarkMode) {
                                            colorScheme.secondaryContainer.copy(alpha = 0.55f)
                                        } else {
                                            colorScheme.secondaryContainer
                                        },
                                        icon = {
                                            androidx.compose.material3.Icon(
                                                Icons.Default.EmojiEvents,
                                                contentDescription = null,
                                                tint = if (isDarkMode) {
                                                    colorScheme.onSecondaryContainer.copy(alpha = 0.95f)
                                                } else {
                                                    colorScheme.onSecondaryContainer
                                                }
                                            )
                                        },
                                        modifier = Modifier.weight(1f)
                                    )

                                    StatBadge(
                                        title = stringResource(R.string.points),
                                        value = profile.totalPoints.toString(),
                                        backgroundColor = if (isDarkMode) {
                                            colorScheme.tertiaryContainer.copy(alpha = 0.60f)
                                        } else {
                                            colorScheme.tertiaryContainer
                                        },
                                        icon = {
                                            androidx.compose.material3.Icon(
                                                Icons.Default.Star,
                                                contentDescription = null,
                                                tint = if (isDarkMode) {
                                                    colorScheme.onTertiaryContainer.copy(alpha = 0.95f)
                                                } else {
                                                    colorScheme.onTertiaryContainer
                                                }
                                            )
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = { navController.navigate("settings") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(18.dp),
                                        elevation = ButtonDefaults.buttonElevation(
                                            defaultElevation = if (isDarkMode) 1.dp else 3.dp,
                                            pressedElevation = if (isDarkMode) 3.dp else 6.dp
                                        ),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isDarkMode) {
                                                colorScheme.primary.copy(alpha = 0.88f)
                                            } else {
                                                colorScheme.primary
                                            },
                                            contentColor = colorScheme.onPrimary
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Settings,
                                            contentDescription = stringResource(R.string.settings),
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text( stringResource(R.string.settings))
                                    }

                                    Button(
                                        onClick = {
                                            navController.navigate("achievements")
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(18.dp),
                                        elevation = ButtonDefaults.buttonElevation(
                                            defaultElevation = if (isDarkMode) 1.dp else 3.dp,
                                            pressedElevation = if (isDarkMode) 3.dp else 6.dp
                                        ),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isDarkMode) {
                                                colorScheme.secondaryContainer.copy(alpha = 0.55f)
                                            } else {
                                                colorScheme.secondaryContainer
                                            },
                                            contentColor = if (isDarkMode) {
                                                colorScheme.onSecondaryContainer.copy(alpha = 0.96f)
                                            } else {
                                                colorScheme.onSecondaryContainer
                                            }
                                        )
                                    ) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(stringResource(R.string.achievements))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = stringResource(R.string.training_stats),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            SmallStatCard(
                                title = stringResource(R.string.total_km),
                                value = totalKmText,
                                icon = Icons.AutoMirrored.Filled.DirectionsRun,
                                iconTint = colorScheme.primary,
                                modifier = Modifier.weight(1f)
                            )

                            SmallStatCard(
                                title = stringResource(R.string.workouts),
                                value = profile.workouts.toString(),
                                icon = Icons.Default.EmojiEvents,
                                iconTint = colorScheme.secondary,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            SmallStatCard(
                                title = stringResource(R.string.current_streak),
                                value = stringResource(R.string.profile_streak_days, profile.streakDays),
                                icon = Icons.Default.LocalFireDepartment,
                                iconTint = colorScheme.error,
                                modifier = Modifier.weight(1f)
                            )

                            SmallStatCard(
                                title = stringResource(R.string.longest_streak),
                                value = stringResource(R.string.profile_streak_days,profile.longestStreak),
                                icon = Icons.Default.LocalFireDepartment,
                                iconTint = colorScheme.tertiary,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { navController.navigate("journey_log") },
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDarkMode) {
                                    colorScheme.primaryContainer.copy(alpha = 0.58f)
                                } else {
                                    colorScheme.primaryContainer
                                }
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = if (isDarkMode) 2.dp else 6.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.profile_current_journey_goal),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = featuredJourneyTitleColor
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = stringResource(R.string.view_your_current_journey_and_completed_routes),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colorScheme.onPrimaryContainer.copy(
                                        alpha = if (isDarkMode) 0.88f else 1f
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = detailsCardColor
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = if (isDarkMode) 2.dp else 5.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.profile_details_title),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.onSurface
                                )

                                ProfileDetailRow(
                                    label = stringResource(R.string.profile_favorite_activity),
                                    value = profile.favoriteActivity,
                                    icon = Icons.AutoMirrored.Filled.DirectionsRun,
                                    iconTint = colorScheme.primary
                                )

                                ProfileDetailRow(
                                    onClick = { navController.navigate("team") },
                                    label = stringResource(R.string.team),
                                    value = profile.teamName,
                                    icon = Icons.Default.Groups,
                                    iconTint = colorScheme.secondary
                                )

                                ProfileDetailRow(
                                    label = stringResource(R.string.profile_location),
                                    value = profile.location,
                                    icon = Icons.Default.Place,
                                    iconTint = colorScheme.tertiary
                                )

                                ProfileDetailRow(
                                    label = stringResource(R.string.profile_member_since),
                                    value = profile.memberSince,
                                    icon = Icons.Default.Star,
                                    iconTint = colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(36.dp))
                    }
                }
            }
        }
    }
}