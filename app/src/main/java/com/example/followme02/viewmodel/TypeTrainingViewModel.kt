package com.example.followme.typetraining

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.roundToInt

data class TypeTrainingUiState(
    val options: List<String> = listOf("Walking", "Running", "Cycling", "Skiing"),
    val selectedOption: String = "Walking",
    val targetText: String = "Walking",
    val userInput: String = "",
    val isRunning: Boolean = false,
    val isCompleted: Boolean = false,
    val elapsedMs: Long = 0L,
    val accuracyPercent: Int = 100,
    val correctChars: Int = 0,
    val errorsCount: Int = 0
)

class TypeTrainingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TypeTrainingUiState())
    val uiState: StateFlow<TypeTrainingUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var startTimeMs: Long = 0L

    fun onSelectOption(option: String) {
        _uiState.value = _uiState.value.copy(
            selectedOption = option,
            targetText = option,
            userInput = "",
            isRunning = false,
            isCompleted = false,
            elapsedMs = 0L,
            accuracyPercent = 100,
            correctChars = 0,
            errorsCount = 0
        )
        stopTimer()
    }

    fun onInputChange(text: String) {
        val state = _uiState.value
        val target = state.targetText

        val compareLen = minOf(text.length, target.length)
        var correct = 0
        var errors = 0

        for (i in 0 until compareLen) {
            if (text[i] == target[i]) correct++ else errors++
        }

        if (text.length > target.length) {
            errors += (text.length - target.length)
        }

        val totalTyped = max(text.length, 1)
        val accuracy = ((correct.toDouble() / totalTyped.toDouble()) * 100.0).roundToInt()

        val completed = text == target
        
        if (completed) {
            stopTimer()
        }

        _uiState.value = state.copy(
            userInput = text,
            correctChars = correct,
            errorsCount = errors,
            accuracyPercent = accuracy,
            isCompleted = completed,
            isRunning = if (completed) false else state.isRunning
        )
    }

    fun start() {
        val state = _uiState.value
        if (!state.isRunning && !state.isCompleted) {
            startTimeMs = System.currentTimeMillis() - state.elapsedMs
            _uiState.value = state.copy(isRunning = true)
            startTimer()
        }
    }

    fun reset() {
        val state = _uiState.value
        stopTimer()
        _uiState.value = state.copy(
            userInput = "",
            isRunning = false,
            isCompleted = false,
            elapsedMs = 0L,
            accuracyPercent = 100,
            correctChars = 0,
            errorsCount = 0
        )
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val elapsed = System.currentTimeMillis() - startTimeMs
                _uiState.value = _uiState.value.copy(elapsedMs = elapsed)
                delay(250)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }
}
