package com.example.followme02.data.repository

import android.util.Log
import com.example.followme02.data.remote.SupabaseProvider
import com.example.followme02.screen.social.FriendRequestUi
import com.example.followme02.screen.social.SearchRelationshipStatus
import com.example.followme02.screen.social.SearchableTeamUi
import com.example.followme02.screen.social.SocialFriendUi
import com.example.followme02.screen.social.SocialTeamUi
import com.example.followme02.screen.social.SocialUserSearchResultUi
import com.example.followme02.screen.social.TeamMemberUi
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.example.followme02.screen.social.SocialActivityUi
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import io.github.jan.supabase.postgrest.query.Columns

@Serializable
private data class SocialDbUserIdRow(
    @SerialName("user_id")
    val userId: Int
)

@Serializable
private data class SocialDbUserRow(
    @SerialName("user_id")
    val userId: Int,
    val username: String,
    val email: String,
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
    @SerialName("level_id")
    val levelId: Int = 1,
    @SerialName("total_points")
    val totalPoints: Int? = null,
    @SerialName("total_accumulated_km")
    val totalAccumulatedKm: Double = 0.0
)

@Serializable
private data class SocialDbFriendshipRow(
    @SerialName("user_id_1")
    val userId1: Int,
    @SerialName("user_id_2")
    val userId2: Int,
    @SerialName("status_id")
    val statusId: Int,
    @SerialName("action_user_id")
    val actionUserId: Int? = null,
    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
private data class SocialDbFriendshipInsertRow(
    @SerialName("user_id_1")
    val userId1: Int,
    @SerialName("user_id_2")
    val userId2: Int,
    @SerialName("status_id")
    val statusId: Int,
    @SerialName("action_user_id")
    val actionUserId: Int
)

@Serializable
private data class SocialDbTeamMembershipRow(
    @SerialName("team_id")
    val teamId: Int,
    @SerialName("user_id")
    val userId: Int,
    @SerialName("joined_at")
    val joinedAt: String? = null
)

@Serializable
private data class SocialDbTeamMembershipInsertRow(
    @SerialName("team_id")
    val teamId: Int,
    @SerialName("user_id")
    val userId: Int
)

@Serializable
private data class SocialDbTrainingSessionActivityRow(
    @SerialName("user_id")
    val userId: Int,
    @SerialName("distance_km")
    val distanceKm: Double? = null,
    @SerialName("created_at")
    val createdAt: String? = null
)
@Serializable
private data class SocialDbTeamRow(
    @SerialName("team_id")
    val teamId: Int,
    @SerialName("team_name")
    val teamName: String,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("leader_user_id")
    val leaderUserId: Int? = null,
    @SerialName("selected_destination_id")
    val selectedDestinationId: Int? = null,
    @SerialName("journey_start_km")
    val journeyStartKm: Double? = null
)

@Serializable
private data class SocialDbTeamInsertRow(
    @SerialName("team_name")
    val teamName: String,
    @SerialName("leader_user_id")
    val leaderUserId: Int
)

@Serializable
private data class SocialDbDestinationRow(
    @SerialName("destination_id")
    val destinationId: Int,
    val name: String,
    @SerialName("km_threshold")
    val kmThreshold: Double = 0.0
)

class SocialRepository {

    private val supabase = SupabaseProvider.client

    companion object {
        private const val STATUS_PENDING = 1
        private const val STATUS_ACCEPTED = 2
        private const val STATUS_BLOCKED = 3
    }

    suspend fun getCurrentDbUserId(): Int? {
        return try {
            val authUserId = supabase.auth.currentUserOrNull()?.id ?: return null

            supabase
                .from("users")
                .select(columns = Columns.list("user_id")) {
                    filter {
                        eq("auth_id", authUserId)
                    }
                }
                .decodeList<SocialDbUserIdRow>()
                .firstOrNull()
                ?.userId
        } catch (e: Exception) {
            Log.e("SOCIAL_REPOSITORY", "Error getting current db user id", e)
            null
        }
    }

    suspend fun getFriends(): List<SocialFriendUi> {
        return try {
            val currentUserId = getCurrentDbUserId() ?: return emptyList()

            val acceptedAsUser1 = supabase
                .from("friendships")
                .select(
                    columns = Columns.list(
                        "user_id_1",
                        "user_id_2",
                        "status_id",
                        "action_user_id",
                        "created_at"
                    )
                ) {
                    filter {
                        eq("user_id_1", currentUserId)
                        eq("status_id", STATUS_ACCEPTED)
                    }
                }
                .decodeList<SocialDbFriendshipRow>()

            val acceptedAsUser2 = supabase
                .from("friendships")
                .select(
                    columns = Columns.list(
                        "user_id_1",
                        "user_id_2",
                        "status_id",
                        "action_user_id",
                        "created_at"
                    )
                ) {
                    filter {
                        eq("user_id_2", currentUserId)
                        eq("status_id", STATUS_ACCEPTED)
                    }
                }
                .decodeList<SocialDbFriendshipRow>()

            (acceptedAsUser1 + acceptedAsUser2).mapNotNull { row ->
                val friendUserId = if (row.userId1 == currentUserId) row.userId2 else row.userId1
                val user = getUserRow(friendUserId) ?: return@mapNotNull null
                user.toSocialFriendUi()
            }.sortedBy { it.username.lowercase() }
        } catch (e: Exception) {
            Log.e("SOCIAL_REPOSITORY", "Error fetching friends", e)
            emptyList()
        }
    }

    suspend fun getFriendRequests(): List<FriendRequestUi> {
        return try {
            val currentUserId = getCurrentDbUserId() ?: return emptyList()

            val pendingAsUser1 = supabase
                .from("friendships")
                .select(
                    columns = Columns.list(
                        "user_id_1",
                        "user_id_2",
                        "status_id",
                        "action_user_id",
                        "created_at"
                    )
                ) {
                    filter {
                        eq("user_id_1", currentUserId)
                        eq("status_id", STATUS_PENDING)
                    }
                }
                .decodeList<SocialDbFriendshipRow>()

            val pendingAsUser2 = supabase
                .from("friendships")
                .select(
                    columns = Columns.list(
                        "user_id_1",
                        "user_id_2",
                        "status_id",
                        "action_user_id",
                        "created_at"
                    )
                ) {
                    filter {
                        eq("user_id_2", currentUserId)
                        eq("status_id", STATUS_PENDING)
                    }
                }
                .decodeList<SocialDbFriendshipRow>()

            (pendingAsUser1 + pendingAsUser2)
                .filter { it.actionUserId != null && it.actionUserId != currentUserId }
                .mapNotNull { row ->
                    val requesterUserId = row.actionUserId ?: return@mapNotNull null
                    val sender = getUserRow(requesterUserId) ?: return@mapNotNull null

                    FriendRequestUi(
                        userId = sender.userId,
                        username = sender.username,
                        email = sender.email,
                        avatarUrl = sender.avatarUrl,
                        level = sender.levelId,
                        totalPoints = sender.totalPoints ?: 0,
                        createdAt = row.createdAt
                    )
                }
                .sortedByDescending { it.createdAt ?: "" }
        } catch (e: Exception) {
            Log.e("SOCIAL_REPOSITORY", "Error fetching friend requests", e)
            emptyList()
        }
    }

    suspend fun searchUserByEmail(emailQuery: String): SocialUserSearchResultUi? {
        return try {
            val currentUserId = getCurrentDbUserId() ?: return null
            val query = emailQuery.trim()
            if (query.isBlank()) return null

            val users = supabase
                .from("users")
                .select(
                    columns = Columns.list(
                        "user_id",
                        "username",
                        "email",
                        "avatar_url",
                        "level_id",
                        "total_points",
                        "total_accumulated_km"
                    )
                ) {
                    filter {
                        or {
                            ilike("email", "%$query%")
                            ilike("username", "%$query%")
                        }
                    }
                }
                .decodeList<SocialDbUserRow>()

            val foundUser = users.firstOrNull { it.userId != currentUserId } ?: return null

            val relationshipStatus = getRelationshipStatus(
                currentUserId = currentUserId,
                otherUserId = foundUser.userId
            )

            SocialUserSearchResultUi(
                userId = foundUser.userId,
                username = foundUser.username,
                email = foundUser.email,
                avatarUrl = foundUser.avatarUrl,
                level = foundUser.levelId,
                totalPoints = foundUser.totalPoints ?: 0,
                totalKm = foundUser.totalAccumulatedKm,
                relationshipStatus = relationshipStatus
            )
        } catch (e: Exception) {
            Log.e("SOCIAL_REPOSITORY", "Error searching user by email", e)
            null
        }
    }

    suspend fun sendFriendRequest(targetUserId: Int) {
        try {
            val currentUserId = getCurrentDbUserId() ?: return
            if (currentUserId == targetUserId) return

            val existingRows = getFriendshipRowsBetween(currentUserId, targetUserId)
            if (existingRows.isNotEmpty()) return

            val (lowId, highId) = normalizeFriendshipPair(currentUserId, targetUserId)

            supabase
                .from("friendships")
                .insert(
                    SocialDbFriendshipInsertRow(
                        userId1 = lowId,
                        userId2 = highId,
                        statusId = STATUS_PENDING,
                        actionUserId = currentUserId
                    )
                )

            Log.d("SOCIAL_REPOSITORY", "Friend request sent from $currentUserId to $targetUserId")
        } catch (e: Exception) {
            Log.e("SOCIAL_REPOSITORY", "Error sending friend request", e)
        }
    }

    suspend fun acceptFriendRequest(requesterUserId: Int) {
        try {
            val currentUserId = getCurrentDbUserId() ?: return
            val (lowId, highId) = normalizeFriendshipPair(currentUserId, requesterUserId)

            supabase
                .from("friendships")
                .update({
                    set("status_id", STATUS_ACCEPTED)
                    set("action_user_id", currentUserId)
                }) {
                    filter {
                        eq("user_id_1", lowId)
                        eq("user_id_2", highId)
                        eq("status_id", STATUS_PENDING)
                    }
                }

            Log.d("SOCIAL_REPOSITORY", "Accepted friend request from $requesterUserId")
        } catch (e: Exception) {
            Log.e("SOCIAL_REPOSITORY", "Error accepting friend request", e)
        }
    }

    suspend fun declineFriendRequest(requesterUserId: Int) {
        try {
            val currentUserId = getCurrentDbUserId() ?: return
            val (lowId, highId) = normalizeFriendshipPair(currentUserId, requesterUserId)

            supabase
                .from("friendships")
                .delete {
                    filter {
                        eq("user_id_1", lowId)
                        eq("user_id_2", highId)
                        eq("status_id", STATUS_PENDING)
                    }
                }

            Log.d("SOCIAL_REPOSITORY", "Declined friend request from $requesterUserId")
        } catch (e: Exception) {
            Log.e("SOCIAL_REPOSITORY", "Error declining friend request", e)
        }
    }

    suspend fun createTeam(teamName: String) {
        try {
            val trimmedName = teamName.trim()
            if (trimmedName.isBlank()) return

            val currentUserId = getCurrentDbUserId() ?: return

            supabase
                .from("team_memberships")
                .delete {
                    filter {
                        eq("user_id", currentUserId)
                    }
                }

            val createdTeam = supabase
                .from("teams")
                .insert(
                    SocialDbTeamInsertRow(
                        teamName = trimmedName,
                        leaderUserId = currentUserId
                    )
                ) {
                    select()
                }
                .decodeList<SocialDbTeamRow>()
                .firstOrNull() ?: return

            supabase
                .from("team_memberships")
                .insert(
                    SocialDbTeamMembershipInsertRow(
                        teamId = createdTeam.teamId,
                        userId = currentUserId
                    )
                )

            Log.d(
                "SOCIAL_REPOSITORY",
                "Created team ${createdTeam.teamName} with leader $currentUserId"
            )
        } catch (e: Exception) {
            Log.e("SOCIAL_REPOSITORY", "Error creating team", e)
        }
    }

    suspend fun getAvailableDestinations(): List<Pair<Int, String>> {
        return try {
            supabase
                .from("destinations")
                .select(columns = Columns.list("destination_id", "name", "km_threshold"))
                .decodeList<SocialDbDestinationRow>()
                .sortedBy { it.kmThreshold }
                .map { it.destinationId to it.name }
        } catch (e: Exception) {
            Log.e("SOCIAL_REPOSITORY", "Error fetching destinations", e)
            emptyList()
        }
    }

    suspend fun setTeamJourney(destinationId: Int) {
        try {
            val currentUserId = getCurrentDbUserId() ?: return
            val team = getCurrentTeamDbRow(currentUserId) ?: return

            if (team.leaderUserId != currentUserId) {
                Log.d(
                    "SOCIAL_REPOSITORY",
                    "Current user is not team leader, cannot set team journey"
                )
                return
            }

            val memberships = supabase
                .from("team_memberships")
                .select(columns = Columns.list("team_id", "user_id")) {
                    filter {
                        eq("team_id", team.teamId)
                    }
                }
                .decodeList<SocialDbTeamMembershipRow>()

            val memberUsers = memberships.mapNotNull { membership ->
                getUserRow(membership.userId)
            }

            val currentTeamTotalKm = memberUsers.sumOf { it.totalAccumulatedKm }

            supabase
                .from("teams")
                .update({
                    set("selected_destination_id", destinationId)
                    set("journey_start_km", currentTeamTotalKm)
                }) {
                    filter {
                        eq("team_id", team.teamId)
                    }
                }

            Log.d(
                "SOCIAL_REPOSITORY",
                "Set team journey for team ${team.teamId} to destination $destinationId"
            )
        } catch (e: Exception) {
            Log.e("SOCIAL_REPOSITORY", "Error setting team journey", e)
        }
    }

    suspend fun getCurrentTeam(): SocialTeamUi? {
        return try {
            val currentUserId = getCurrentDbUserId() ?: return null
            val team = getCurrentTeamDbRow(currentUserId) ?: return null
            buildSocialTeamUi(team, currentUserId)
        } catch (e: Exception) {
            Log.e("SOCIAL_REPOSITORY", "Error fetching current team", e)
            null
        }
    }

    suspend fun getTeamMembers(teamId: Int): List<TeamMemberUi> {
        return try {
            val currentUserId = getCurrentDbUserId()

            val memberships = supabase
                .from("team_memberships")
                .select(columns = Columns.list("team_id", "user_id")) {
                    filter {
                        eq("team_id", teamId)
                    }
                }
                .decodeList<SocialDbTeamMembershipRow>()

            memberships.mapNotNull { membership ->
                val user = getUserRow(membership.userId) ?: return@mapNotNull null

                TeamMemberUi(
                    userId = user.userId,
                    username = user.username,
                    email = user.email,
                    avatarUrl = user.avatarUrl,
                    level = user.levelId,
                    totalPoints = user.totalPoints ?: 0,
                    totalKm = user.totalAccumulatedKm,
                    isCurrentUser = user.userId == currentUserId
                )
            }.sortedByDescending { it.totalPoints }
        } catch (e: Exception) {
            Log.e("SOCIAL_REPOSITORY", "Error fetching team members", e)
            emptyList()
        }
    }

    suspend fun getRecentTeamActivity(): List<SocialActivityUi> {
        return try {
            val currentUserId = getCurrentDbUserId() ?: return emptyList()
            val team = getCurrentTeamDbRow(currentUserId) ?: return emptyList()

            val memberships = supabase
                .from("team_memberships")
                .select(columns = Columns.list("team_id", "user_id", "joined_at")) {
                    filter {
                        eq("team_id", team.teamId)
                    }
                }
                .decodeList<SocialDbTeamMembershipRow>()

            val joinActivities = memberships.mapNotNull { membership ->
                val user = getUserRow(membership.userId) ?: return@mapNotNull null

                val title = if (user.userId == currentUserId) {
                    "You joined ${team.teamName}"
                } else {
                    "${user.username} joined ${team.teamName}"
                }

                val description = if (user.userId == currentUserId) {
                    "You became a member of this team."
                } else {
                    "${user.username} became a member of your team."
                }

                SocialActivityUi(
                    title = title,
                    description = description,
                    createdAt = membership.joinedAt
                )
            }

            val workoutActivities = memberships.flatMap { membership ->
                val user = getUserRow(membership.userId) ?: return@flatMap emptyList()

                val sessions = supabase
                    .from("training_sessions")
                    .select(columns = Columns.list("user_id", "distance_km", "created_at")) {
                        filter {
                            eq("user_id", membership.userId)
                        }
                    }
                    .decodeList<SocialDbTrainingSessionActivityRow>()

                sessions.map { session ->
                    val distanceText = formatKmForActivity(session.distanceKm ?: 0.0)

                    val title = if (user.userId == currentUserId) {
                        "You logged a workout"
                    } else {
                        "${user.username} logged a workout"
                    }

                    val description = if (user.userId == currentUserId) {
                        "You added $distanceText km to the team progress."
                    } else {
                        "${user.username} added $distanceText km to the team progress."
                    }

                    SocialActivityUi(
                        title = title,
                        description = description,
                        createdAt = session.createdAt
                    )
                }
            }

            (joinActivities + workoutActivities)
                .sortedByDescending { it.createdAt ?: "" }
                .take(4)
        } catch (e: Exception) {
            Log.e("SOCIAL_REPOSITORY", "Error fetching recent team activity", e)
            emptyList()
        }
    }

    suspend fun getAllTeams(): List<SearchableTeamUi> {
        return try {
            val currentTeam = getCurrentTeam()

            val teams = supabase
                .from("teams")
                .select(columns = Columns.list("team_id", "team_name"))
                .decodeList<SocialDbTeamRow>()

            val memberships = supabase
                .from("team_memberships")
                .select(columns = Columns.list("team_id", "user_id"))
                .decodeList<SocialDbTeamMembershipRow>()

            teams.map { team ->
                SearchableTeamUi(
                    teamId = team.teamId,
                    teamName = team.teamName,
                    memberCount = memberships.count { it.teamId == team.teamId },
                    isCurrentTeam = team.teamId == currentTeam?.teamId
                )
            }.sortedBy { it.teamName.lowercase() }
        } catch (e: Exception) {
            Log.e("SOCIAL_REPOSITORY", "Error fetching all teams", e)
            emptyList()
        }
    }

    suspend fun joinTeam(teamId: Int) {
        try {
            val currentUserId = getCurrentDbUserId() ?: return

            supabase
                .from("team_memberships")
                .delete {
                    filter {
                        eq("user_id", currentUserId)
                    }
                }

            supabase
                .from("team_memberships")
                .insert(
                    SocialDbTeamMembershipInsertRow(
                        teamId = teamId,
                        userId = currentUserId
                    )
                )

            Log.d("SOCIAL_REPOSITORY", "User $currentUserId joined team $teamId")
        } catch (e: Exception) {
            Log.e("SOCIAL_REPOSITORY", "Error joining team", e)
        }
    }

    private suspend fun getCurrentTeamDbRow(currentUserId: Int): SocialDbTeamRow? {
        val membership = supabase
            .from("team_memberships")
            .select(columns = Columns.list("team_id", "user_id")) {
                filter {
                    eq("user_id", currentUserId)
                }
            }
            .decodeList<SocialDbTeamMembershipRow>()
            .firstOrNull() ?: return null

        return supabase
            .from("teams")
            .select(
                columns = Columns.list(
                    "team_id",
                    "team_name",
                    "created_at",
                    "leader_user_id",
                    "selected_destination_id",
                    "journey_start_km"
                )
            ) {
                filter {
                    eq("team_id", membership.teamId)
                }
            }
            .decodeList<SocialDbTeamRow>()
            .firstOrNull()
    }

    private suspend fun buildSocialTeamUi(
        team: SocialDbTeamRow,
        currentUserId: Int
    ): SocialTeamUi {
        val memberships = supabase
            .from("team_memberships")
            .select(columns = Columns.list("team_id", "user_id")) {
                filter {
                    eq("team_id", team.teamId)
                }
            }
            .decodeList<SocialDbTeamMembershipRow>()

        val memberUsers = memberships.mapNotNull { membership ->
            getUserRow(membership.userId)
        }

        val memberCount = memberUsers.size
        val teamTotalKm = memberUsers.sumOf { it.totalAccumulatedKm }
        val journeyStartKm = team.journeyStartKm ?: 0.0

        val destination = team.selectedDestinationId?.let { destinationId ->
            supabase
                .from("destinations")
                .select(columns = Columns.list("destination_id", "name", "km_threshold")) {
                    filter {
                        eq("destination_id", destinationId)
                    }
                }
                .decodeList<SocialDbDestinationRow>()
                .firstOrNull()
        }

        val targetKm = destination?.kmThreshold ?: 0.0
        val rawProgressKm = if (team.selectedDestinationId != null) {
            (teamTotalKm - journeyStartKm).coerceAtLeast(0.0)
        } else {
            0.0
        }

        val progressFraction = if (targetKm > 0.0) {
            (rawProgressKm / targetKm).coerceIn(0.0, 1.0).toFloat()
        } else {
            0f
        }

        return SocialTeamUi(
            teamId = team.teamId,
            teamName = team.teamName,
            memberCount = memberCount,
            createdAt = team.createdAt,
            leaderUserId = team.leaderUserId,
            isCurrentUserLeader = team.leaderUserId == currentUserId,
            destinationId = team.selectedDestinationId,
            destinationName = destination?.name ?: "No team goal yet",
            targetKm = targetKm,
            progressKm = rawProgressKm,
            progressFraction = progressFraction
        )
    }

    private suspend fun getRelationshipStatus(
        currentUserId: Int,
        otherUserId: Int
    ): SearchRelationshipStatus {
        if (currentUserId == otherUserId) {
            return SearchRelationshipStatus.SELF
        }

        val rows = getFriendshipRowsBetween(currentUserId, otherUserId)
        val row = rows.firstOrNull() ?: return SearchRelationshipStatus.NONE

        return when (row.statusId) {
            STATUS_ACCEPTED -> SearchRelationshipStatus.FRIEND
            STATUS_BLOCKED -> SearchRelationshipStatus.NONE
            STATUS_PENDING -> {
                when (row.actionUserId) {
                    currentUserId -> SearchRelationshipStatus.PENDING_SENT
                    otherUserId -> SearchRelationshipStatus.PENDING_RECEIVED
                    else -> SearchRelationshipStatus.NONE
                }
            }

            else -> SearchRelationshipStatus.NONE
        }
    }

    private suspend fun getFriendshipRowsBetween(
        currentUserId: Int,
        otherUserId: Int
    ): List<SocialDbFriendshipRow> {
        val (lowId, highId) = normalizeFriendshipPair(currentUserId, otherUserId)

        return supabase
            .from("friendships")
            .select(
                columns = Columns.list(
                    "user_id_1",
                    "user_id_2",
                    "status_id",
                    "action_user_id",
                    "created_at"
                )
            ) {
                filter {
                    eq("user_id_1", lowId)
                    eq("user_id_2", highId)
                }
            }
            .decodeList<SocialDbFriendshipRow>()
    }

    private suspend fun getUserRow(userId: Int): SocialDbUserRow? {
        return try {
            supabase
                .from("users")
                .select(
                    columns = Columns.list(
                        "user_id",
                        "username",
                        "email",
                        "avatar_url",
                        "level_id",
                        "total_points",
                        "total_accumulated_km"
                    )
                ) {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<SocialDbUserRow>()
                .firstOrNull()
        } catch (e: Exception) {
            Log.e("SOCIAL_REPOSITORY", "Error fetching user row for userId=$userId", e)
            null
        }
    }

    private fun formatKmForActivity(km: Double): String {
        return if (km % 1.0 == 0.0) {
            km.toInt().toString()
        } else {
            "%.1f".format(km)
        }
    }

    private fun normalizeFriendshipPair(userA: Int, userB: Int): Pair<Int, Int> {
        return if (userA < userB) userA to userB else userB to userA
    }

    private fun SocialDbUserRow.toSocialFriendUi(): SocialFriendUi {
        return SocialFriendUi(
            userId = userId,
            username = username,
            email = email,
            avatarUrl = avatarUrl,
            level = levelId,
            totalPoints = totalPoints ?: 0,
            totalKm = totalAccumulatedKm
        )
    }

    suspend fun getFriendIds(): List<Int> {
        val friendships = getFriends() // dere har denne allerede
        return friendships.map { it.userId }
    }

    suspend fun getFriendActivities(): List<SocialActivityUi> {
        return try {
            val friendIds = getFriendIds()

            val activities = supabase
                .from("training_sessions")
                .select(columns = Columns.list("user_id", "distance_km", "created_at")) {
                    filter {
                        isIn("user_id", friendIds)
                    }
                }
                .decodeList<SocialDbTrainingSessionActivityRow>()

            activities.mapNotNull { session ->
                val user = getUserRow(session.userId) ?: return@mapNotNull null

                SocialActivityUi(
                    title = "${user.username} trained",
                    description = "${formatKmForActivity(session.distanceKm ?: 0.0)} km",
                    createdAt = session.createdAt
                )
            }

        } catch (e: Exception) {
            Log.e("SOCIAL_REPOSITORY", "Error fetching friend activities", e)
            emptyList()
        }
    }

    @Serializable
    data class JoinRequestRow(
        val request_id: String,
        val team_id: Int,
        val user_id: Int,
        val status: String,
        val username: String = ""
    )

    @Serializable
    data class JoinRequestInsert(
        val team_id: Int,
        val user_id: Int,
        val status: String = "PENDING"
    )

    @Serializable
    data class TeamInviteInsert(
        val team_id: Int,
        val invited_user_id: Int,
        val invited_by: Int,
        val status: String = "PENDING"
    )

    @Serializable
    data class TeamInviteRow(
        val id: Int,
        val team_id: Int,
        val invited_user_id: Int,
        val invited_by: Int,
        val status: String,
        val teams: TeamNameWrapper? = null
    )

    @Serializable
    data class TeamNameWrapper(
        val team_name: String
    )

    // Sender Team Join Request
    suspend fun requestToJoinTeam(teamId: Int) {
        val userId = getCurrentDbUserId()

        // Debug user
        Log.d("JOIN_DEBUG", "userId = $userId")

        if (userId == null) {
            Log.e("JOIN_ERROR", "userId is NULL")
            return
        }

        try {
            supabase.from("join_requests").insert(
                JoinRequestInsert(
                    team_id = teamId,
                    user_id = userId
                )
            )

            Log.d("JOIN_DEBUG", "INSERT SUCCESS")

        } catch (e: Exception) {
            Log.e("JOIN_ERROR", "Insert failed: ${e.message}")
        }
    }

    // Henter join requests + legger til username manuelt
    suspend fun getJoinRequests(teamId: Int): List<JoinRequestRow> {

        // 1. hent requests
        val requests = supabase
            .from("join_requests")
            .select(columns = Columns.list("request_id", "team_id", "user_id", "status")) {
                filter {
                    eq("team_id", teamId)
                    eq("status", "pending")
                }
            }
            .decodeList<JoinRequestRow>()

        // 2. hent username for hver request
        return requests.map { req ->
            val user = getUserRow(req.user_id)

            req.copy(
                username = user?.username ?: "Unknown"
            )
        }
    }

    // Godkjenning av request
    suspend fun approveJoinRequest(req: JoinRequestRow) {

        try {
            Log.d("APPROVE_DEBUG", "Start approve: user=${req.user_id}, team=${req.team_id}")

            // 🔹 fjern bruker fra alle teams først
            supabase.from("team_memberships")
                .delete {
                    filter {
                        eq("user_id", req.user_id)
                    }
                }

            Log.d("APPROVE_DEBUG", "Deleted old memberships")

            // 🔹 legg til i nytt team
            supabase.from("team_memberships").insert(
                SocialDbTeamMembershipInsertRow(
                    teamId = req.team_id,
                    userId = req.user_id
                )
            )

            Log.d("APPROVE_DEBUG", "Inserted new membership")

            // 🔹 oppdater request
            supabase.from("join_requests")
                .update(mapOf("status" to "APPROVED")) {
                    filter {
                        eq("request_id", req.request_id)
                    }
                }

            Log.d("APPROVE_DEBUG", "Updated request")

        } catch (e: Exception) {
            Log.e("APPROVE_ERROR", "CRASH: ${e.message}", e)
        }
    }

    suspend fun denyJoinRequest(req: JoinRequestRow) {
        supabase.from("join_requests")
            .update(
                mapOf("status" to "DENIED")
            ) {
                filter {
                    eq("request_id", req.request_id)
                }
            }
    }

    // Kicker bruker
    suspend fun kickUser(teamId: Int, userId: Int) {
        supabase
            .from("team_memberships")
            .delete {
                filter {
                    eq("team_id", teamId)
                    eq("user_id", userId)
                }
            }
    }

    suspend fun getMyJoinRequests(): List<JoinRequestRow> {
        val userId = getCurrentDbUserId() ?: return emptyList()

        return try {
            supabase
                .from("join_requests")
                .select {
                    filter {
                        eq("user_id", userId)
                        eq("status", "PENDING")
                    }
                }
                .decodeList<JoinRequestRow>()
        } catch (e: Exception) {
            Log.e("JOIN_ERROR", "Failed to fetch my join requests: ${e.message}")
            emptyList()
        }
    }

    suspend fun inviteUserToTeam(teamId: Int, userId: Int) {
        val currentUserId = getCurrentDbUserId() ?: return

        try {
            val data = buildJsonObject {
                put("team_id", teamId)
                put("invited_user_id", userId)
                put("invited_by", currentUserId)
                put("status", "pending")
            }

            supabase.from("team_invites").insert(data)

            Log.d("INVITE_DEBUG", "Invite success")
        } catch (e: Exception) {
            Log.e("INVITE_ERROR", "Invite failed: ${e.message}")
        }
    }

    suspend fun getInvitesForUser(): List<TeamInviteRow> {
        val userId = getCurrentDbUserId() ?: return emptyList()

        val invites = supabase
            .from("team_invites")
            .select {
                filter {
                    eq("invited_user_id", userId)
                    eq("status", "pending")
                }
            }
            .decodeList<TeamInviteRow>()

        // MANUELL HENTING AV TEAM NAVN
        return invites.map { invite ->
            val team = supabase
                .from("teams")
                .select {
                    filter {
                        eq("team_id", invite.team_id)
                    }
                }
                .decodeList<TeamNameWrapper>()
                .firstOrNull()

            invite.copy(
                teams = team
            )
        }
    }

    suspend fun acceptInvite(req: TeamInviteRow) {
        val userId = getCurrentDbUserId() ?: return

        try {
            // fjern fra team
            supabase.from("team_memberships")
                .delete {
                    filter { eq("user_id", userId) }
                }

            // join nytt team
            supabase.from("team_memberships")
                .insert(
                    SocialDbTeamMembershipInsertRow(
                        teamId = req.team_id,
                        userId = userId
                    )
                )

            //  SLETT invite direkte (ingen update først)
            supabase.from("team_invites")
                .delete {
                    filter {
                        eq("invited_user_id", userId)
                        eq("team_id", req.team_id)
                    }
                }

            Log.d("INVITE_DEBUG", "Accept success")

        } catch (e: Exception) {
            Log.e("INVITE_ERROR", "Accept failed: ${e.message}")
        }
    }

    suspend fun declineInvite(req: TeamInviteRow) {
        val userId = getCurrentDbUserId() ?: return

        try {
            Log.d("INVITE_DEBUG", "Declining invite for user=$userId team=${req.team_id}")

            supabase.from("team_invites")
                .delete {
                    filter {
                        eq("invited_user_id", userId)
                        eq("team_id", req.team_id)
                    }
                }

            Log.d("INVITE_DEBUG", "Decline DELETE success")

        } catch (e: Exception) {
            Log.e("INVITE_ERROR", "Decline failed: ${e.message}")
        }
    }

    suspend fun leaveTeam() {
        val userId = getCurrentDbUserId() ?: return

        try {
            // Fjern fra team
            supabase.from("team_memberships")
                .delete {
                    filter {
                        eq("user_id", userId)
                    }
                }

            //  Fjern gamle invites (optional cleanup)
            supabase.from("team_invites")
                .delete {
                    filter {
                        eq("invited_user_id", userId)
                    }
                }

            Log.d("TEAM_DEBUG", "Left team")

        } catch (e: Exception) {
            Log.e("TEAM_ERROR", "Leave failed: ${e.message}")
        }
    }
}




