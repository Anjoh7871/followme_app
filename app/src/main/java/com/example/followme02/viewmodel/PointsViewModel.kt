package com.example.followme02.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.followme02.data.repository.PointsRepository
import com.example.followme02.model.ExerciseType
import kotlinx.coroutines.launch

class PointsViewModel(
    private val repository: PointsRepository = PointsRepository()
) : ViewModel() {

    var userPoints by mutableStateOf(0)
        private set

    fun fetchPoints(userId: String) {
        viewModelScope.launch {
            userPoints = repository.getUserPoints(userId)
        }
    }

    fun addPoints(userId: String, distance: Long, type: ExerciseType) {
        viewModelScope.launch {
            repository.addPoints(userId, distance, type)
            userPoints = repository.getUserPoints(userId)
        }
    }
}