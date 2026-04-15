package com.example.followme02.screen.achievements

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.followme02.data.repository.AchievementRepository
import kotlinx.coroutines.launch
import kotlin.collections.map

class AchievementViewModel(
    private val repository: AchievementRepository = AchievementRepository()
) : ViewModel() {

    var achievementsList by mutableStateOf<List<AchievementUiState>>(emptyList())
    var totalUnlocked by mutableStateOf(0)
    var isLoading by mutableStateOf(true)

    init { loadData() }

    fun loadData() {
        viewModelScope.launch {
            isLoading = true

            val internalId = repository.getInternalUserId()

            if (internalId == null) {
                Log.e("ACHIEVEMENT_VM", "Internal ID is null")
                isLoading = false
                return@launch
            }

            val defs = repository.getDefinitions()
            val sessions = repository.getTrainingSessions(internalId)
            var unlocked = repository.getUnlocked(internalId)

            Log.d("ACHIEVEMENT_VM", "Definitions count: ${defs.size}")
            Log.d("ACHIEVEMENT_VM", "Sessions count: ${sessions.size}")
            Log.d("ACHIEVEMENT_VM", "Unlocked before check: ${unlocked.size}")

            defs.forEach { def ->
                val alreadyUnlocked = unlocked.any { it.achievementId == def.achievementId }
                val currentValue = calculateCurrentValue(def, sessions)
                val targetValue = def.reqValue?.toDouble() ?: 0.0
                val shouldUnlock = targetValue > 0 && currentValue >= targetValue

                Log.d(
                    "ACHIEVEMENT_VM",
                    "Achievement ${def.achievementId} (${def.title}) | reqUnitId=${def.reqUnitId} | currentValue=$currentValue | targetValue=$targetValue | alreadyUnlocked=$alreadyUnlocked | shouldUnlock=$shouldUnlock"
                )

                if (!alreadyUnlocked && shouldUnlock) {
                    val success = repository.unlockAchievement(
                        userId = internalId,
                        achievementId = def.achievementId
                    )

                    Log.d(
                        "ACHIEVEMENT_VM",
                        "Tried to unlock achievement ${def.achievementId}, success=$success"
                    )
                }
            }

            unlocked = repository.getUnlocked(internalId)
            Log.d("ACHIEVEMENT_VM", "Unlocked after check: ${unlocked.size}")

            achievementsList = defs.map { def ->
                val earned = unlocked.find { it.achievementId == def.achievementId }
                val currentValue = calculateCurrentValue(def, sessions)
                val target = def.reqValue?.toDouble() ?: 1.0
                val isUnlockedNow = earned != null || currentValue >= target

                AchievementUiState(
                    title = def.title,
                    description = def.description ?: "",
                    isUnlocked = isUnlockedNow,
                    progress = (currentValue / target).toFloat().coerceIn(0f, 1f),
                    progressLabel = "${currentValue.toInt()} / ${def.reqValue ?: 0}",
                    unlockedDate = earned?.earnedAt?.split("T")?.get(0)
                )
            }

            totalUnlocked = achievementsList.count { it.isUnlocked }
            isLoading = false
        }
    }
    private fun calculateCurrentValue(def: com.example.followme02.model.Achievement, sessions: List<com.example.followme02.model.TrainingSessions>): Double {
        return when (def.reqUnitId) {
            1L -> sessions.size.toDouble() // session
            2L -> sessions.sumOf { it.distanceKm.toDouble() } // km
            else -> 0.0
        }
    }
}
/*class AchievementViewModel(
    private val repository: AchievementRepository = AchievementRepository()
) : ViewModel() {

    var achievementsList by mutableStateOf<List<AchievementUiState>>(emptyList())
    var totalUnlocked by mutableStateOf(0)
    var isLoading by mutableStateOf(true)


    init { loadData() }

    private fun loadData() {
        viewModelScope.launch {
            //val internalId = repository.getInternalUserId() ?: return@launch
            //val defs = repository.getDefinitions()
            //val unlocked = repository.getUnlocked(internalId)
            //val sessions = repository.getTrainingSessions(internalId)

            isLoading = true
            //val authId = repository.getAuthId() // Get the UUID first
            //println("DEBUG: Auth UUID is $authId")

            val internalId = repository.getInternalUserId()
            println("DEBUG: Internal Integer ID is $internalId")

            if (internalId == null) {
                println("DEBUG: Failed because Internal ID is null. Check your 'users' table!")
                isLoading = false
                return@launch
            }

            val defs = repository.getDefinitions()
            println("DEBUG: Found ${defs.size} achievement definitions")

            val unlocked = repository.getUnlocked(internalId)
            println("DEBUG: Found ${unlocked.size} unlocked achievements")

            val sessions = repository.getTrainingSessions(internalId)
            println("DEBUG: Found ${sessions.size} training sessions")


            // Logic calculations performed here
            achievementsList = defs.map { def ->
                val earned = unlocked.find { it.achievementId == def.achievementId }

                // Calculate current progress based on type_id
                // 1 = Total Distance, 2 = Session Count, etc.
                val currentValue = when (def.typeId) {
                    1L -> sessions.sumOf { it.distanceKm }
                    2L -> sessions.size.toDouble()
                    else -> 0.0
                }
                val targetValue = def.reqValue?.toDouble() ?: 1.0
                val progressValue = (currentValue / targetValue).toFloat().coerceIn(0f, 1f)

                AchievementUiState(
                    title = def.title,
                    description = def.description ?: "",
                    isUnlocked = earned != null,
                    progress = progressValue,
                    progressLabel = "${currentValue.toInt()} / ${def.reqValue}",
                    unlockedDate = earned?.earnedAt?.split("T")?.get(0)
                )
            }
            totalUnlocked = unlocked.size
            isLoading = false
        }
    }
}*/