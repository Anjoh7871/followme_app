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
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.followme02.R
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

    val previewFriends = if (state.friends.isEmpty()) {
        listOf(
            SocialFriendUi(
                userId = 1,
                username = "CocoChou",
                email = "coco@email.com",
                avatarUrl = null,
                totalKm = 24.5,
                totalPoints = 180,
                level = 3
            )
        )
    } else {
        state.friends
    }

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
                            title = stringResource(R.string.something_went_wrong),
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
                                        title = stringResource(R.string.find_user_by_email),
                                        value = state.friendSearchQuery,
                                        onValueChange = viewModel::onFriendSearchQueryChange,
                                        placeholder = stringResource(R.string.example_email_com),
                                        buttonText = stringResource(R.string.search),
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
                                            title = stringResource(R.string.search_result),
                                            description = state.friendSearchMessage
                                        )
                                    }
                                }

                                item {
                                    SectionHeader(
                                        title = stringResource(R.string.your_friends),
                                        trailing = stringResource(
                                            R.string.friends,
                                            previewFriends.size
                                        )
                                    )
                                }

                                items(previewFriends) { friend ->
                                    FriendRow(
                                        friend = friend,
                                        onClick = {
                                            selectedFriend = friend
                                            currentSheet = SocialSheetType.FRIEND_PROFILE
                                        }
                                    )
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

                                // 🔹 YOUR TEAM
                                item {
                                    SectionHeader(title = stringResource(R.string.your_team))
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
                                            title = stringResource(R.string.no_team),
                                            description = stringResource(R.string.create_a_team_below_or_join_an_existing_one)
                                        )
                                    }
                                }

                                // Join request for leader
                                if (state.currentTeam?.isCurrentUserLeader == true) {

                                    item {
                                        SectionHeader(
                                            title = stringResource(R.string.join_requests),
                                            trailing = "${state.joinRequests.size}"
                                        )
                                    }

                                    if (state.joinRequests.isEmpty()) {
                                        item {
                                            EmptyStateCard(
                                                title = stringResource(R.string.no_pending_requests),
                                                description = stringResource(R.string.no_pending_requests_desc)
                                            )
                                        }
                                    } else {
                                        items(state.joinRequests) { req ->   // 🔥 FIX HER

                                            Card {
                                                Row(
                                                    modifier = Modifier.padding(12.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = stringResource(
                                                            R.string.user_label,
                                                            req.user_id
                                                        )
                                                    )

                                                    Button(
                                                        onClick = {
                                                            viewModel.approveJoinRequest(req)
                                                        }
                                                    ) {
                                                        Text(stringResource(R.string.approve))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }


                                val allActivity = (state.recentTeamActivity + state.friendActivities)
                                    .sortedByDescending { it.createdAt ?: "" }

                                item {
                                    SectionHeader(
                                        title = stringResource(R.string.recent_activity),
                                        trailing = stringResource(
                                            R.string.updates,
                                            allActivity.size
                                        )
                                    )
                                }

                                if (allActivity.isEmpty()) {
                                    item {
                                        EmptyStateCard(
                                            title = stringResource(R.string.no_recent_activity_yet),
                                            description = stringResource(R.string.activity_feed_description)
                                        )
                                    }
                                } else {
                                    items(allActivity) { activity ->
                                        RecentActivityRow(
                                            title = activity.title,
                                            description = formatActivityDescription(activity)
                                        )
                                    }
                                }

                                item {
                                    SocialSearchCard(
                                        title = stringResource(R.string.create_team),
                                        value = state.createTeamName,
                                        onValueChange = viewModel::onCreateTeamNameChange,
                                        placeholder = stringResource(R.string.team_name),
                                        buttonText = stringResource(R.string.create_team),
                                        onButtonClick = viewModel::createTeam,
                                        leadingIcon = Icons.Default.Groups
                                    )
                                }

                                item {
                                    SocialSearchCard(
                                        title = stringResource(R.string.find_a_team),
                                        value = state.teamSearchQuery,
                                        onValueChange = viewModel::onTeamSearchQueryChange,
                                        placeholder = stringResource(R.string.search_teams),
                                        buttonText = stringResource(R.string.filter),
                                        onButtonClick = { }
                                    )
                                }

                                item {
                                    SectionHeader(
                                        title = stringResource(R.string.available_teams),
                                        trailing = stringResource(
                                            R.string.teams,
                                            filteredTeams.size
                                        )
                                    )
                                }

                                if (filteredTeams.isEmpty()) {
                                    item {
                                        EmptyStateCard(
                                            title = stringResource(R.string.no_teams_found),
                                            description = stringResource(R.string.try_another_team_name)
                                        )
                                    }
                                } else {
                                    items(filteredTeams) { team ->
                                        TeamSearchRow(
                                            team = team,
                                            onJoinClick = { viewModel.requestToJoinTeam(team.teamId) }
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
                title = stringResource(R.string.notifications),
                onClose = { currentSheet = SocialSheetType.NONE }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                if (state.friendRequests.isEmpty()) {
                    EmptyStateCard(
                        title = stringResource(R.string.no_notifications),
                        description = stringResource(R.string.right_now_notifications_are_based_on_real_friend_requests_from_the_database)
                    )
                } else {
                    state.friendRequests.forEach { request ->
                        NotificationRow(
                            title = stringResource(R.string.friend_request),
                            description = stringResource(
                                R.string.wants_to_be_your_friend,
                                request.username
                            )
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
                title = stringResource(R.string.friend_requests),
                onClose = { currentSheet = SocialSheetType.NONE }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                if (state.friendRequests.isEmpty()) {
                    EmptyStateCard(
                        title = stringResource(R.string.no_friend_requests),
                        description = stringResource(R.string.you_are_all_caught_up)
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
                title = stringResource(R.string.friend_profile),
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
                text = stringResource(R.string.social),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.see_friends_search_for_users_manage_teams_and_track_social_activity),
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
                    contentDescription = stringResource(R.string.notifications),
                    tint = colorScheme.onBackground
                )
            }
        }
    }
}

private fun formatActivityDescription(activity: SocialActivityUi): String {
    val date = activity.createdAt?.take(10)

    return if (date.isNullOrBlank()) {
        activity.description
    } else {
        "${activity.description}\n$date"
    }
}