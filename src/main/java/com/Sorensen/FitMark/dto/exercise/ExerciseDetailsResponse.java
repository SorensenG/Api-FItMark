package com.Sorensen.FitMark.dto.exercise;

public record ExerciseDetailsResponse(
        String exerciseName,
        String workoutName,
        int sets,
        int reps,
        double maxWeight,
        double lastWeight,
        int lastReps


) {
}
