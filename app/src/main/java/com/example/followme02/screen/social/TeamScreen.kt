package com.example.followme02.screen.social

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.followme02.screen.profile.ProfileAvatar
import com.example.followme02.viewmodel.SocialViewModel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.ui.res.stringResource
import com.example.followme02.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamScreen(
    navController: NavController,
    viewModel: SocialViewModel = viewModel()
) {
    val state = viewModel.uiState.value
    val colorScheme = MaterialTheme.colorScheme
    val destinations = viewModel.availableDestinations.value
    val leaderboardMembers = viewModel.getSortedLeaderboard()
    var showInviteSheet by remember { mutableStateOf(false) }
    var inviteQuery by remember { mutableStateOf("") }
    var inviteResult by remember { mutableStateOf<SocialUserSearchResultUi?>(null) }

    var showJourneyPicker by remember { mutableStateOf(false) }
    var selectedMember by remember { mutableStateOf<TeamMemberUi?>(null) }
    var showLeaveTeamDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadSocialData()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorScheme.background
    ) {
        if (state.isLoading && state.currentTeam == null) {
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
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.cd_go_back),
                                tint = colorScheme.onBackground
                            )
                        }

                        Text(
                            text = stringResource(R.string.team),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = colorScheme.onBackground
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stringResource(R.string.team_leaderboard_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                if (state.errorMessage != null) {
                    item {
                        EmptyStateCard(
                            title = stringResource(R.string.error_generic_title),
                            description = state.errorMessage
                        )
                    }
                }

                item {
                    if (state.currentTeam != null) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TeamJourneyCard(
                                team = state.currentTeam,
                                isLeader = state.currentTeam.isCurrentUserLeader,
                                onClick = {
                                    if (state.currentTeam.isCurrentUserLeader) {
                                        showJourneyPicker = true
                                    }
                                }
                            )
                            // INVITE BUTTON (leader only)
                            if (state.currentTeam.isCurrentUserLeader) {
                                Button(
                                    onClick = {
                                        showInviteSheet = true
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text("Invite new member")
                                }
                            }

                            if (!state.currentTeam.isCurrentUserLeader) {
                                OutlinedButton(
                                    onClick = { showLeaveTeamDialog = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(stringResource(R.string.leave_team))
                                }
                            }
                        }
                    } else {
                        EmptyStateCard(
                            title = stringResource(R.string.not_in_team_title),
                            description = stringResource(R.string.not_in_team_description)
                        )
                    }
                }
                if (state.invites.isNotEmpty()) {

                    item {
                        Text(
                            text = "Team Invites",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(state.invites) { invite ->

                        val teamName = invite.teams?.team_name ?: "Team ${invite.team_id}"

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {

                                Text(
                                    text = "You are invited to $teamName",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {

                                    Button(
                                        onClick = {
                                            viewModel.acceptInvite(invite)
                                        }
                                    ) {
                                        Text("Accept")
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            viewModel.declineInvite(invite)
                                        }
                                    ) {
                                        Text("Decline")
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    SectionHeader(title = stringResource(R.string.leaderboard))
                }

                item {
                    LeaderboardTypeSelector(
                        selectedType = state.leaderboardType,
                        onTypeSelected = viewModel::onLeaderboardTypeSelected
                    )
                }

                if (leaderboardMembers.isEmpty()) {
                    item {
                        EmptyStateCard(
                            title = stringResource(R.string.no_leaderboard_title),
                            description = stringResource(R.string.no_leaderboard_description)
                        )
                    }
                } else {
                    items(leaderboardMembers) { member ->
                        Column {

                            TeamLeaderboardRow(
                                member = member,
                                rank = leaderboardMembers.indexOf(member) + 1,
                                selectedType = state.leaderboardType,
                                onClick = {
                                    selectedMember = member
                                }
                            )

                            // 🔹 ACTION BUTTONS (LEADER ONLY)
                            if (state.currentTeam?.isCurrentUserLeader == true &&
                                !member.isCurrentUser
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.End
                                ) {


                                    Spacer(modifier = Modifier.width(8.dp))

                                    TextButton(
                                        onClick = {
                                            viewModel.kickUser(
                                                teamId = state.currentTeam!!.teamId,
                                                userId = member.userId
                                            )
                                        }
                                    ) {
                                        Text(
                                            text = stringResource(R.string.kick),
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }


                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }

        if (showJourneyPicker && state.currentTeam != null) {
            ModalBottomSheet(
                onDismissRequest = { showJourneyPicker = false },
                containerColor = colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    BottomSheetHeader(
                        title = stringResource(R.string.choose_team_journey),
                        onClose = { showJourneyPicker = false }
                    )

                    if (destinations.isEmpty()) {
                        EmptyStateCard(
                            title = stringResource(R.string.no_destinations_title),
                            description = stringResource(R.string.no_destinations_description)
                        )
                    } else {
                        destinations.forEach { destination ->
                            val isSelected = state.currentTeam.destinationId == destination.first

                            if (isSelected) {
                                ElevatedButton(
                                    onClick = {
                                        viewModel.setTeamJourney(destination.first)
                                        showJourneyPicker = false
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(stringResource(R.string.selected) + " ${destination.second}")
                                }
                            } else {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.setTeamJourney(destination.first)
                                        showJourneyPicker = false
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(destination.second)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        if (selectedMember != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedMember = null },
                containerColor = colorScheme.surface
            ) {
                BottomSheetHeader(
                    title = stringResource(R.string.member_profile),
                    onClose = { selectedMember = null }
                )

                TeamMemberProfileContent(member = selectedMember!!)

                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        if (showLeaveTeamDialog) {
            AlertDialog(
                onDismissRequest = { showLeaveTeamDialog = false },
                title = {
                    Text(stringResource(R.string.leave_team_title))
                },
                text = {
                    Text(stringResource(R.string.leave_team_confirm))
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showLeaveTeamDialog = false
                            viewModel.leaveTeam()
                        }
                    ) {
                        Text(stringResource(R.string.leave))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showLeaveTeamDialog = false }
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
    // INVITE SHEET (LEGG HER)
    if (showInviteSheet) {
        ModalBottomSheet(
            onDismissRequest = { showInviteSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                Text(
                    text = "Invite user",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = inviteQuery,
                    onValueChange = { inviteQuery = it },
                    label = { Text("Search username or email") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        viewModel.onFriendSearchQueryChange(inviteQuery)
                        viewModel.searchUserByEmail()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Search")
                }

                val result = viewModel.uiState.value.friendSearchResult

                result?.let { user ->

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = user.username,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val teamId = state.currentTeam?.teamId ?: return@Button

                            viewModel.inviteUserToTeam(
                                teamId = teamId,
                                userId = user.userId
                            )
                            showInviteSheet = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Invite")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}


@Composable
private fun TeamJourneyCard(
    team: SocialTeamUi,
    isLeader: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()

    Card(
        modifier = if (isLeader) {
            Modifier
                .fillMaxWidth()
                .clickable { onClick() }
        } else {
            Modifier.fillMaxWidth()
        },
        shape = RoundedCornerShape(20.dp),
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
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = team.teamName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stringResource(R.string.team_journey),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onPrimaryContainer.copy(alpha = 0.78f)
                    )

                    if (isLeader) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.tap_to_change_destination),
                            style = MaterialTheme.typography.bodySmall,
                            color = colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(colorScheme.primary.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = null,
                        tint = colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = null,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = team.destinationName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            LinearProgressIndicator(
                progress = { team.progressFraction },
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
                    text = "${(team.progressFraction * 100).toInt()}% " + stringResource(R.string.complete),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
                )

                Text(
                    text = "${formatKm(team.progressKm)} / ${formatKm(team.targetKm)} " + stringResource(R.string.km),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onPrimaryContainer.copy(alpha = 0.78f)
                )
            }
        }
    }
}

@Composable
fun TeamLeaderboardRow(
    member: TeamMemberUi,
    rank: Int,
    selectedType: LeaderboardType,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()

    val valueText = when (selectedType) {
        LeaderboardType.POINTS -> "${member.totalPoints} " + stringResource(R.string.pts)
        LeaderboardType.KM -> "${formatKm(member.totalKm)} " + stringResource(R.string.km)
    }

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
            Text(
                text = "#$rank",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary
            )

            Spacer(modifier = Modifier.width(14.dp))

            ProfileAvatar(
                username = member.username,
                avatarUrl = member.avatarUrl,
                modifier = Modifier.size(54.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

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
                            text = stringResource(R.string.you),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = stringResource(R.string.tap_to_view_profile),
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = valueText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TeamMemberProfileContent(
    member: TeamMemberUi
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ProfileAvatar(
                username = member.username,
                avatarUrl = member.avatarUrl,
                modifier = Modifier.size(74.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.username,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = member.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.level) + " ${member.level}",
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
            TeamMiniStatCard(
                title = stringResource(R.string.points),
                value = member.totalPoints.toString(),
                modifier = Modifier.weight(1f)
            )

            TeamMiniStatCard(
                title = stringResource(R.string.total_km),
                value = formatKm(member.totalKm),
                modifier = Modifier.weight(1f)
            )

            TeamMiniStatCard(
                title = stringResource(R.string.level),
                value = member.level.toString(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TeamMiniStatCard(
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

private fun formatKm(km: Double): String {
    return if (km % 1.0 == 0.0) {
        km.toInt().toString()
    } else {
        "%.1f".format(km)
    }
}
