package com.example.followme02.screen.achievements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.followme02.ui.theme.achievementUnlockedBottomTextDark
import com.example.followme02.ui.theme.achievementUnlockedBottomTextLight
import com.example.followme02.ui.theme.achievementUnlockedBorderDark
import com.example.followme02.ui.theme.achievementUnlockedBorderLight
import com.example.followme02.ui.theme.achievementUnlockedCardDark
import com.example.followme02.ui.theme.achievementUnlockedCardLight
import com.example.followme02.ui.theme.achievementUnlockedDescriptionDark
import com.example.followme02.ui.theme.achievementUnlockedDescriptionLight
import com.example.followme02.ui.theme.achievementUnlockedIconDark
import com.example.followme02.ui.theme.achievementUnlockedIconLight
import com.example.followme02.ui.theme.achievementUnlockedTitleDark
import com.example.followme02.ui.theme.achievementUnlockedTitleLight
import com.example.followme02.ui.theme.backgroundDark
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.remember


private fun imageUrlToDrawableName(imageUrl: String?): String? {
    if (imageUrl.isNullOrBlank()) return null

    return imageUrl
        .substringAfterLast("/")
        .substringBeforeLast(".")
        .lowercase()
}

@Composable
fun AchievementScreen(navController: NavController) {
    val viewModel: AchievementViewModel = viewModel()
    val achievements = viewModel.achievementsList
    val totalUnlocked = viewModel.totalUnlocked
    val isLoading = viewModel.isLoading
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Gamification Hub 🎮",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Track your achievements and compete with others!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(18.dp))

                AchievementTabs(navController = navController)

                Spacer(modifier = Modifier.height(20.dp))

                ProgressOverviewCard(
                    totalUnlocked = totalUnlocked,
                    totalAchievements = achievements.size
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = colorScheme.primary)
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 120.dp)
                    ) {
                        items(achievements) { item ->
                            AchievementItem(item = item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AchievementTabs(
    navController: NavController
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {
            Button(
                onClick = { },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.surface,
                    contentColor = colorScheme.onSurface
                ),
                shape = RoundedCornerShape(14.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = "Achievements",
                    fontWeight = FontWeight.SemiBold
                )
            }

            TextButton(
                onClick = { navController.navigate("leaderboard") },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Leaderboard",
                    color = colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ProgressOverviewCard(
    totalUnlocked: Int,
    totalAchievements: Int
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = colorScheme.background == backgroundDark
    val progress =
        if (totalAchievements == 0) 0f else totalUnlocked.toFloat() / totalAchievements.toFloat()

    val goldContainer = if (isDark) {
        Color(0xFF3A2E05)
    } else {
        Color(0xFFFFF8E6)
    }

    val goldBorder = if (isDark) {
        Color(0xFFD97706)
    } else {
        Color(0xFFF4D06F)
    }

    val titleColor = if (isDark) {
        Color(0xFFFFE082)
    } else {
        colorScheme.onSurface
    }

    val subtitleColor = if (isDark) {
        Color(0xFFFFB74D)
    } else {
        Color(0xFFB4690E)
    }

    val progressColor = if (isDark) {
        Color(0xFFFFC107)
    } else {
        Color(0xFFF4B400)
    }

    val trackColor = if (isDark) {
        Color(0xFF4A3A0A)
    } else {
        colorScheme.secondaryContainer.copy(alpha = 0.5f)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = goldContainer),
        border = BorderStroke(1.dp, goldBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Your Progress",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )

                Text(
                    text = "$totalUnlocked / $totalAchievements Unlocked",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = subtitleColor
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(CircleShape),
                color = progressColor,
                trackColor = trackColor,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap
            )
        }
    }
}

@Composable
fun AchievementItem(item: AchievementUiState) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = colorScheme.background == backgroundDark
    val context = LocalContext.current

    val unlockedCard = if (isDark) {
        Color(0xFF6A5310)
    } else {
        Color(0xFFFFF8E6)
    }

    val unlockedBorder = if (isDark) {
        Color(0xFFE0B84A)
    } else {
        Color(0xFFF4D06F)
    }

    val unlockedIconBox = if (isDark) {
        Color(0xFFF2C14E)
    } else {
        Color(0xFFF59E0B)
    }

    val unlockedTitle = if (isDark) {
        Color(0xFFFFF4CC)
    } else {
        Color(0xFF92400E)
    }

    val unlockedDesc = if (isDark) {
        Color(0xFFFFE7A3)
    } else {
        Color(0xFFB45309)
    }

    val unlockedBottom = if (isDark) {
        Color(0xFFFFD166)
    } else {
        Color(0xFFD97706)
    }

    val lockedCard = if (isDark) {
        colorScheme.surfaceContainerHigh
    } else {
        colorScheme.surfaceContainerLow
    }


    val drawableName = remember(item.iconUrl) {
        imageUrlToDrawableName(item.iconUrl)
    }

    val imageResId = remember(drawableName) {
        drawableName?.let {
            context.resources.getIdentifier(it, "drawable", context.packageName)
        } ?: 0
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isUnlocked) unlockedCard else lockedCard
        ),
        border = BorderStroke(
            1.dp,
            if (item.isUnlocked) unlockedBorder else colorScheme.outlineVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (item.isUnlocked) unlockedIconBox else colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageResId != 0) {
                        Image(
                            painter = painterResource(id = imageResId),
                            contentDescription = item.title,
                            modifier = Modifier.size(24.dp),
                            contentScale = ContentScale.Fit,
                            alpha = if (item.isUnlocked) 1f else 0.45f
                        )
                    } else {
                        Icon(
                            imageVector = if (item.isUnlocked) Icons.Default.Star else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (item.isUnlocked) Color(0xFF4A3600) else colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (item.isUnlocked) unlockedTitle else colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium,
                color = if (item.isUnlocked) unlockedDesc else colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.weight(1f))

            if (item.isUnlocked) {
                Text(
                    text = "Unlocked ${item.unlockedDate}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = unlockedBottom
                )
            } else {
                Text(
                    text = item.progressLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { item.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = if (isDark) Color(0xFFFFC107) else colorScheme.primary,
                    trackColor = if (isDark) Color(0xFF4A3A0A) else colorScheme.surfaceVariant,
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap
                )
            }
        }
    }
}