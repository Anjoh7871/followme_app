package com.example.followme02.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.followme02.data.repository.WorkoutRepository
import com.example.followme02.model.Workout
import kotlinx.coroutines.launch

class WorkoutViewModel : ViewModel() {

    private val repository = WorkoutRepository()

    var workouts = mutableStateOf<List<Workout>>(emptyList())
        private set

    var isLoading = mutableStateOf(false)
        private set

    var message = mutableStateOf<String?>(null)
        private set

    fun loadWorkouts() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                workouts.value = repository.getWorkouts()
            } catch (e: Exception) {
                message.value = "Kunne ikke hente treningsøkter."
            } finally {
                isLoading.value = false
            }
        }
    }

    fun addWorkout(workout: Workout) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val success = repository.saveWorkout(workout)

                if (success) {
                    workouts.value = repository.getWorkouts()
                    message.value = "Ny treningsøkt lagret."
                } else {
                    message.value = "Kunne ikke lagre treningsøkta."
                }
            } catch (e: Exception) {
                message.value = "Noe gikk galt ved lagring."
            } finally {
                isLoading.value = false
            }
        }
    }

    fun clearMessage() {
        message.value = null
    }
}