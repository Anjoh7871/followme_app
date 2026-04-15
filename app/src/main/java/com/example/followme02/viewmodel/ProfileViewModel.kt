package com.example.followme02.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.followme02.data.repository.UserRepository
import com.example.followme02.data.repository.AchievementRepository
import com.example.followme02.data.repository.WorkoutRepository
import com.example.followme02.model.ExerciseType
import com.example.followme02.screen.profile.ProfileUiState
import com.example.followme02.screen.profile.fakeProfileUiState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.navigation.NavController
import com.example.followme02.data.remote.SupabaseProvider
import io.github.jan.supabase.auth.auth

class ProfileViewModel : ViewModel() {

    private val repository = UserRepository()
    private val achievementRepository = AchievementRepository()
    private val workoutRepository = WorkoutRepository()

    var uiState = mutableStateOf(ProfileUiState())
        private set

    var isLoading = mutableStateOf(false)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    fun loadUser() {
        viewModelScope.launch {
            isLoading.value = true

            try {
                val result = repository.getProfile()

                if (result != null) {

                    val sessions = achievementRepository
                        .getTrainingSessions(achievementRepository.getInternalUserId() ?: 0)

                    val workouts = workoutRepository.getWorkouts()

                    val totalKm = sessions.sumOf { it.distanceKm }
                    val favoriteActivity = getFavoriteActivity(workouts)
                    val streak = getWorkoutStreak(workouts)
                    val longestStreak = getLongestStreak(workouts)
                    val teamName = repository.getUserTeamName()

                    uiState.value = fakeProfileUiState.copy(
                        username = result.username,
                        email = result.email,
                        avatarUrl = result.avatarUrl,
                        currentLevel = result.currentLevel,
                        totalPoints = result.totalPoints,
                        totalAccumulatedKm = totalKm,
                        goalCurrentKm = totalKm.toFloat(),
                        workouts = workouts.size,
                        streakDays = streak,
                        longestStreak = longestStreak,
                        favoriteActivity = favoriteActivity,
                        teamName = teamName,
                        memberSince = formatMemberSince(result.createdAt)
                    )
                }

            } catch (e: Exception) {
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun getFavoriteActivity(workouts: List<com.example.followme02.model.Workout>): String {
        if (workouts.isEmpty()) return "No activity yet"

        return workouts
            .groupingBy { it.exerciseType }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key
            ?.name
            ?.lowercase()
            ?.replaceFirstChar { it.uppercase() }
            ?: "No activity"
    }

    private fun getWorkoutStreak(workouts: List<com.example.followme02.model.Workout>): Int {
        val dates = workouts.mapNotNull { parseDate(it.date) }
            .map { normalize(it) }
            .distinct()
            .sortedDescending()

        if (dates.isEmpty()) return 0

        val today = normalize(Date())
        val yesterday = Calendar.getInstance().apply {
            time = today
            add(Calendar.DAY_OF_YEAR, -1)
        }.time

        if (dates.first() != today && dates.first() != yesterday) return 0

        return countStreak(dates)
    }

    private fun getLongestStreak(workouts: List<com.example.followme02.model.Workout>): Int {
        val dates = workouts.mapNotNull { parseDate(it.date) }
            .map { normalize(it) }
            .distinct()
            .sortedDescending()

        if (dates.isEmpty()) return 0

        var longest = 1
        var current = 1

        for (i in 1 until dates.size) {
            val prev = dates[i - 1]
            val expected = Calendar.getInstance().apply {
                time = prev
                add(Calendar.DAY_OF_YEAR, -1)
            }.time

            if (dates[i] == expected) {
                current++
                longest = maxOf(longest, current)
            } else {
                current = 1
            }
        }

        return longest
    }

    private fun countStreak(dates: List<Date>): Int {
        var streak = 1

        for (i in 1 until dates.size) {
            val prev = dates[i - 1]
            val expected = Calendar.getInstance().apply {
                time = prev
                add(Calendar.DAY_OF_YEAR, -1)
            }.time

            if (dates[i] == expected) {
                streak++
            } else break
        }

        return streak
    }

    private fun parseDate(date: String?): Date? {
        return try {
            SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date ?: "")
        } catch (e: Exception) {
            null
        }
    }

    private fun normalize(date: Date): Date {
        return Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }

    private fun formatMemberSince(dateString: String?): String {
        if (dateString == null) return "Unknown"

        return try {
            val input = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val output = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
            output.format(input.parse(dateString.substring(0, 10))!!)
        } catch (e: Exception) {
            "Unknown"
        }
    }

    fun logout(navController: NavController) {
        viewModelScope.launch {
            SupabaseProvider.client.auth.signOut()
            navController.navigate("login") { popUpTo(0) }
        }
    }
}