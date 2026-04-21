package com.example.followme02.screen.social

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.followme02.screen.profile.ProfileAvatar
import androidx.compose.foundation.BorderStroke

@Composable
fun SocialTabSelector(
    selectedTab: SocialTab,
    onTabSelected: (SocialTab) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) colorScheme.surfaceContainer else colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDark) 2.dp else 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onTabSelected(SocialTab.FRIENDS) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == SocialTab.FRIENDS) {
                        colorScheme.primary
                    } else {
                        colorScheme.surfaceVariant
                    },
                    contentColor = if (selectedTab == SocialTab.FRIENDS) {
                        colorScheme.onPrimary
                    } else {
                        colorScheme.onSurfaceVariant
                    }
                )
            ) {
                Text("Friends")
            }

            Button(
                onClick = { onTabSelected(SocialTab.TEAMS) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == SocialTab.TEAMS) {
                        colorScheme.primary
                    } else {
                        colorScheme.surfaceVariant
                    },
                    contentColor = if (selectedTab == SocialTab.TEAMS) {
                        colorScheme.onPrimary
                    } else {
                        colorScheme.onSurfaceVariant
                    }
                )
            ) {
                Text("Teams")
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    trailing: String? = null
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground
        )

        if (trailing != null) {
            Text(
                text = trailing,
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SocialSearchCard(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    leadingIcon: ImageVector = Icons.Default.Search
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) colorScheme.surfaceContainer else colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDark) 2.dp else 5.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(placeholder) },
                singleLine = true,
                leadingIcon = {
                    Icon(leadingIcon, contentDescription = null)
                },
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorScheme.primary,
                    unfocusedBorderColor = colorScheme.outlineVariant,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onButtonClick,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(buttonText)
            }
        }
    }
}

@Composable
fun FriendRequestsBanner(
    requestCount: Int,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(colorScheme.secondary.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    tint = colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Friend Requests",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSecondaryContainer
                )

                Text(
                    text = "$requestCount pending requests",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSecondaryContainer.copy(alpha = 0.82f)
                )
            }
        }
    }
}

@Composable
fun FriendRow(
    friend: SocialFriendUi,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) colorScheme.surfaceContainer else colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDark) 2.dp else 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileAvatar(
                username = friend.username,
                avatarUrl = friend.avatarUrl,
                modifier = Modifier.size(58.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = friend.username,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Lv ${friend.level} · ${friend.totalPoints} pts · ${formatKm(friend.totalKm)} km",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SearchResultCard(
    result: SocialUserSearchResultUi,
    actionLabel: String,
    actionEnabled: Boolean,
    onActionClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProfileAvatar(
                    username = result.username,
                    avatarUrl = result.avatarUrl,
                    modifier = Modifier.size(58.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = result.username,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onPrimaryContainer
                    )

                    Text(
                        text = result.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onPrimaryContainer.copy(alpha = 0.82f)
                    )

                    Text(
                        text = "Lv ${result.level} · ${result.totalPoints} pts · ${formatKm(result.totalKm)} km",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onPrimaryContainer.copy(alpha = 0.82f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onActionClick,
                enabled = actionEnabled,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(actionLabel)
            }
        }
    }
}

@Composable
fun EmptyStateCard(
    title: String,
    description: String
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) colorScheme.surfaceContainer else colorScheme.surface
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
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TeamOverviewCard(
    team: SocialTeamUi,
    onViewMembersClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(colorScheme.primary.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = team.teamName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = colorScheme.onPrimaryContainer
                    )

                    Text(
                        text = "${team.memberCount} members",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onPrimaryContainer.copy(alpha = 0.82f)
                    )
                }

                TextButton(onClick = onViewMembersClick) {
                    Text("Members")
                }
            }
        }
    }
}

@Composable
fun LeaderboardTypeSelector(
    selectedType: LeaderboardType,
    onTypeSelected: (LeaderboardType) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedType == LeaderboardType.POINTS,
            onClick = { onTypeSelected(LeaderboardType.POINTS) },
            label = { Text("Points") }
        )

        FilterChip(
            selected = selectedType == LeaderboardType.KM,
            onClick = { onTypeSelected(LeaderboardType.KM) },
            label = { Text("Distance") }
        )
    }
}

@Composable
fun LeaderboardCard(
    members: List<TeamMemberUi>,
    selectedType: LeaderboardType
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()

    if (members.isEmpty()) {
        EmptyStateCard(
            title = "No leaderboard yet",
            description = "Join a team first, then the leaderboard will show up here."
        )
        return
    }

    val maxValue = when (selectedType) {
        LeaderboardType.POINTS -> members.maxOf { it.totalPoints }.coerceAtLeast(1)
        LeaderboardType.KM -> members.maxOf { it.totalKm.toInt() }.coerceAtLeast(1)
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) colorScheme.surfaceContainer else colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDark) 2.dp else 5.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            members.forEachIndexed { index, member ->
                LeaderboardRow(
                    rank = index + 1,
                    member = member,
                    selectedType = selectedType,
                    maxValue = maxValue
                )

                if (index != members.lastIndex) {
                    HorizontalDivider(
                        color = colorScheme.outlineVariant.copy(alpha = 0.35f)
                    )
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRow(
    rank: Int,
    member: TeamMemberUi,
    selectedType: LeaderboardType,
    maxValue: Int
) {
    val colorScheme = MaterialTheme.colorScheme

    val currentValue = when (selectedType) {
        LeaderboardType.POINTS -> member.totalPoints
        LeaderboardType.KM -> member.totalKm.toInt()
    }

    val progress = currentValue.toFloat() / maxValue.toFloat()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "#$rank",
            modifier = Modifier.width(34.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurfaceVariant
        )

        ProfileAvatar(
            username = member.username,
            avatarUrl = member.avatarUrl,
            modifier = Modifier.size(46.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = member.username,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )

                if (member.isCurrentUser) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "You",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .height(8.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(colorScheme.primary)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = when (selectedType) {
                LeaderboardType.POINTS -> "${member.totalPoints} pts"
                LeaderboardType.KM -> "${formatKm(member.totalKm)} km"
            },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TeamSearchRow(
    team: SearchableTeamUi,
    onJoinClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) colorScheme.surfaceContainer else colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDark) 2.dp else 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colorScheme.secondary.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = null,
                    tint = colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = team.teamName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )

                Text(
                    text = "${team.memberCount} members",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onJoinClick,
                enabled = !team.isCurrentTeam,
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(if (team.isCurrentTeam) "Joined" else "Join")
            }
        }
    }
}

@Composable
fun FriendRequestRow(
    request: FriendRequestUi,
    onAcceptClick: () -> Unit,
    onDeclineClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) colorScheme.surfaceContainer else colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProfileAvatar(
                    username = request.username,
                    avatarUrl = request.avatarUrl,
                    modifier = Modifier.size(56.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = request.username,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )

                    Text(
                        text = request.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "Lv ${request.level} · ${request.totalPoints} pts",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onAcceptClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Accept")
                }

                Button(
                    onClick = onDeclineClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.surfaceVariant,
                        contentColor = colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Decline")
                }
            }
        }
    }
}

@Composable
fun TeamMemberRow(
    member: TeamMemberUi
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) colorScheme.surfaceContainer else colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileAvatar(
                username = member.username,
                avatarUrl = member.avatarUrl,
                modifier = Modifier.size(52.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = member.username,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )

                    if (member.isCurrentUser) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "You",
                            style = MaterialTheme.typography.labelMedium,
                            color = colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "Lv ${member.level} · ${member.totalPoints} pts · ${formatKm(member.totalKm)} km",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun NotificationRow(
    title: String,
    description: String
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) colorScheme.surfaceContainer else colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(colorScheme.primary.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun RecentActivityRow(
    title: String,
    description: String
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isDark) {
                colorScheme.primary.copy(alpha = 0.18f)
            } else {
                colorScheme.outlineVariant.copy(alpha = 0.9f)
            }
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) {
                colorScheme.primaryContainer.copy(alpha = 0.42f)
            } else {
                colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDark) 1.dp else 3.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isDark) {
                            colorScheme.primary.copy(alpha = 0.16f)
                        } else {
                            colorScheme.primaryContainer.copy(alpha = 0.65f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = if (isDark) {
                        colorScheme.primary
                    } else {
                        colorScheme.onPrimaryContainer
                    }
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun FriendProfileContent(
    friend: SocialFriendUi
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
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

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Level ${friend.level}",
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
            MiniStatCard(
                title = "Points",
                value = friend.totalPoints.toString(),
                modifier = Modifier.weight(1f)
            )
            MiniStatCard(
                title = "Total km",
                value = formatKm(friend.totalKm),
                modifier = Modifier.weight(1f)
            )
            MiniStatCard(
                title = "Level",
                value = friend.level.toString(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MiniStatCard(
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
            containerColor = if (isDark) colorScheme.surfaceContainer else colorScheme.surface
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
fun BottomSheetHeader(
    title: String,
    onClose: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = colorScheme.onSurface
        )

        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close"
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