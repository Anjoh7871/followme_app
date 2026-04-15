package com.example.followme02.data.logic

import com.example.followme02.model.ExerciseType

object PointsCalculator {
    fun calculate(
        distance: Long,
        exerciseType: ExerciseType
    ): Int {
        return when (exerciseType) {
            ExerciseType.RUN -> (distance * 15).toInt()
            ExerciseType.CYCLE -> (distance * 5).toInt()
            ExerciseType.SKI -> (distance * 5).toInt()
            else -> (distance * 10).toInt()
        }
    }
}