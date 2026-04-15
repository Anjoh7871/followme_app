package com.example.followme02.screen.social

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.followme02.viewmodel.SocialViewModel

private enum class SocialSheetType {
    NONE,
    NOTIFICATIONS,
    FRIEND_REQUESTS,
    FRIEND_PROFILE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialScreen(
    navController: NavController,
    viewModel: SocialViewModel = viewModel()
) {
    val state = viewModel.uiState.value
    val colorScheme = MaterialTheme.colorScheme

    var currentSheet by remember { mutableStateOf(SocialSheetType.NONE) }
    var selectedFriend by remember { mutableStateOf<SocialFriendUi?>(null) }

    val filteredTeams = viewModel.getFilteredTeams()

    LaunchedEffect(Unit) {
        viewModel.loadSocialData()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (state.isLoading && state.friends.isEmpty() && state.availableTeams.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colorScheme.primary)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    SocialHeader(
                        notificationCount = state.friendRequests.size,
                        onNotificationsClick = { currentSheet = SocialSheetType.NOTIFICATIONS }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SocialTabSelector(
                        selectedTab = state.selectedTab,
                        onTabSelected = viewModel::onTabSelected
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (state.errorMessage != null) {
                        EmptyStateCard(
                            title = "Something went wrong",
                            description = state.errorMessage
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    when (state.selectedTab) {
                        SocialTab.FRIENDS -> {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                if (state.friendRequests.isNotEmpty()) {
                                    item {
                                        FriendRequestsBanner(
                                            requestCount = state.friendRequests.size,
                                            onClick = { currentSheet = SocialSheetType.FRIEND_REQUESTS }
                                        )
                                    }
                                }

                                item {
                                    SocialSearchCard(
                                        title = "Find user by email",
                                        value = state.friendSearchQuery,
                                        onValueChange = viewModel::onFriendSearchQueryChange,
                                        placeholder = "example@email.com",
                                        buttonText = "Search",
                                        onButtonClick = viewModel::searchUserByEmail
                                    )
                                }

                                if (state.friendSearchResult != null) {
                                    item {
                                        SearchResultCard(
                                            result = state.friendSearchResult,
                                            actionLabel = viewModel.getSearchActionLabel(),
                                            actionEnabled = viewModel.isSearchActionEnabled(),
                                            onActionClick = viewModel::onSearchAction
                                        )
                                    }
                                }

                                if (state.friendSearchMessage != null) {
                                    item {
                                        EmptyStateCard(
                                            title = "Search result",
                                            description = state.friendSearchMessage
                                        )
                                    }
                                }

                                item {
                                    SectionHeader(
                                        title = "Your Friends",
                                        trailing = "${state.friends.size} friends"
                                    )
                                }

                                if (state.friends.isEmpty()) {
                                    item {
                                        EmptyStateCard(
                                            title = "No friends yet",
                                            description = "Search for users by email and start building your friend list."
                                        )
                                    }
                                } else {
                                    items(state.friends) { friend ->
                                        FriendRow(
                                            friend = friend,
                                            onClick = {
                                                selectedFriend = friend
                                                currentSheet = SocialSheetType.FRIEND_PROFILE
                                            }
                                        )
                                    }
                                }

                                item {
                                    Spacer(modifier = Modifier.height(24.dp))
                                }
                            }
                        }

                        SocialTab.TEAMS -> {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                item {
                                    SectionHeader(title = "Your Team")
                                }

                                item {
                                    if (state.currentTeam != null) {
                                        Box(
                                            modifier = Modifier.clickable {
                                                navController.navigate("team")
                                            }
                                        ) {
                                            TeamOverviewCard(
                                                team = state.currentTeam,
                                                onViewMembersClick = {
                                                    navController.navigate("team")
                                                }
                                            )
                                        }
                                    } else {
                                        EmptyStateCard(
                                            title = "You are not in a team",
                                            description = "Create a team below or join an existing one."
                                        )
                                    }
                                }

                                item {
                                    SocialSearchCard(
                                        title = "Create a team",
                                        value = state.createTeamName,
                                        onValueChange = viewModel::onCreateTeamNameChange,
                                        placeholder = "Team name...",
                                        buttonText = "Create team",
                                        onButtonClick = viewModel::createTeam
                                    )
                                }

                                item {
                                    SectionHeader(title = "Recent Activity")
                                }

                                item {
                                    EmptyStateCard(
                                        title = "No recent team activity yet",
                                        description = "This section is ready in the UI, but we need to connect it to real team activity data next."
                                    )
                                }

                                item {
                                    SocialSearchCard(
                                        title = "Find a team",
                                        value = state.teamSearchQuery,
                                        onValueChange = viewModel::onTeamSearchQueryChange,
                                        placeholder = "Search teams...",
                                        buttonText = "Filter",
                                        onButtonClick = { }
                                    )
                                }

                                item {
                                    SectionHeader(
                                        title = "Available Teams",
                                        trailing = "${filteredTeams.size} teams"
                                    )
                                }

                                if (filteredTeams.isEmpty()) {
                                    item {
                                        EmptyStateCard(
                                            title = "No teams found",
                                            description = "Try another team name."
                                        )
                                    }
                                } else {
                                    items(filteredTeams) { team ->
                                        TeamSearchRow(
                                            team = team,
                                            onJoinClick = { viewModel.joinTeam(team.teamId) }
                                        )
                                    }
                                }

                                item {
                                    Spacer(modifier = Modifier.height(24.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (currentSheet == SocialSheetType.NOTIFICATIONS) {
        ModalBottomSheet(
            onDismissRequest = { currentSheet = SocialSheetType.NONE },
            containerColor = colorScheme.surface
        ) {
            BottomSheetHeader(
                title = "Notifications",
                onClose = { currentSheet = SocialSheetType.NONE }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                if (state.friendRequests.isEmpty()) {
                    EmptyStateCard(
                        title = "No notifications",
                        description = "Right now notifications are based on real friend requests from the database."
                    )
                } else {
                    state.friendRequests.forEach { request ->
                        NotificationRow(
                            title = "Friend request",
                            description = "${request.username} wants to be your friend"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (currentSheet == SocialSheetType.FRIEND_REQUESTS) {
        ModalBottomSheet(
            onDismissRequest = { currentSheet = SocialSheetType.NONE },
            containerColor = colorScheme.surface
        ) {
            BottomSheetHeader(
                title = "Friend Requests",
                onClose = { currentSheet = SocialSheetType.NONE }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                if (state.friendRequests.isEmpty()) {
                    EmptyStateCard(
                        title = "No friend requests",
                        description = "You are all caught up."
                    )
                } else {
                    state.friendRequests.forEach { request ->
                        FriendRequestRow(
                            request = request,
                            onAcceptClick = { viewModel.acceptFriendRequest(request.userId) },
                            onDeclineClick = { viewModel.declineFriendRequest(request.userId) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (currentSheet == SocialSheetType.FRIEND_PROFILE && selectedFriend != null) {
        ModalBottomSheet(
            onDismissRequest = {
                currentSheet = SocialSheetType.NONE
                selectedFriend = null
            },
            containerColor = colorScheme.surface
        ) {
            BottomSheetHeader(
                title = "Friend Profile",
                onClose = {
                    currentSheet = SocialSheetType.NONE
                    selectedFriend = null
                }
            )

            FriendProfileContent(friend = selectedFriend!!)

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SocialHeader(
    notificationCount: Int,
    onNotificationsClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Social",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "See friends, search for users, manage teams, and track social activity.",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )
        }

        BadgedBox(
            badge = {
                if (notificationCount > 0) {
                    Badge {
                        Text(notificationCount.toString())
                    }
                }
            }
        ) {
            IconButton(onClick = onNotificationsClick) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = colorScheme.onBackground
                )
            }
        }
    }
}