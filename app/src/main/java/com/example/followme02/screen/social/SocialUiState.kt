package com.example.followme02.screen.social

enum class SocialTab {
    FRIENDS,
    TEAMS
}

enum class LeaderboardType {
    POINTS,
    KM
}

enum class SearchRelationshipStatus {
    NONE,
    SELF,
    FRIEND,
    PENDING_SENT,
    PENDING_RECEIVED
}

data class SocialFriendUi(
    val userId: Int,
    val username: String,
    val email: String,
    val avatarUrl: String? = null,
    val level: Int = 1,
    val totalPoints: Int = 0,
    val totalKm: Double = 0.0
)

data class FriendRequestUi(
    val userId: Int,
    val username: String,
    val email: String,
    val avatarUrl: String? = null,
    val level: Int = 1,
    val totalPoints: Int = 0,
    val createdAt: String? = null
)

data class SocialUserSearchResultUi(
    val userId: Int,
    val username: String,
    val email: String,
    val avatarUrl: String? = null,
    val level: Int = 1,
    val totalPoints: Int = 0,
    val totalKm: Double = 0.0,
    val relationshipStatus: SearchRelationshipStatus = SearchRelationshipStatus.NONE
)

data class SocialTeamUi(
    val teamId: Int,
    val teamName: String,
    val memberCount: Int = 0,
    val createdAt: String? = null,
    val leaderUserId: Int? = null,
    val isCurrentUserLeader: Boolean = false,
    val destinationId: Int? = null,
    val destinationName: String = "No team goal yet",
    val targetKm: Double = 0.0,
    val progressKm: Double = 0.0,
    val progressFraction: Float = 0f
)

data class TeamMemberUi(
    val userId: Int,
    val username: String,
    val email: String,
    val avatarUrl: String? = null,
    val level: Int = 1,
    val totalPoints: Int = 0,
    val totalKm: Double = 0.0,
    val isCurrentUser: Boolean = false
)

data class SearchableTeamUi(
    val teamId: Int,
    val teamName: String,
    val memberCount: Int = 0,
    val isCurrentTeam: Boolean = false
)

data class SocialActivityUi(
    val title: String,
    val description: String,
    val createdAt: String? = null
)

data class SocialUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    val selectedTab: SocialTab = SocialTab.FRIENDS,
    val leaderboardType: LeaderboardType = LeaderboardType.POINTS,

    val friendSearchQuery: String = "",
    val friendSearchResult: SocialUserSearchResultUi? = null,
    val friendSearchMessage: String? = null,

    val teamSearchQuery: String = "",
    val createTeamName: String = "",

    val friends: List<SocialFriendUi> = emptyList(),
    val friendRequests: List<FriendRequestUi> = emptyList(),

    val currentTeam: SocialTeamUi? = null,
    val teamMembers: List<TeamMemberUi> = emptyList(),
    val availableTeams: List<SearchableTeamUi> = emptyList(),
    val recentTeamActivity: List<SocialActivityUi> = emptyList()
)