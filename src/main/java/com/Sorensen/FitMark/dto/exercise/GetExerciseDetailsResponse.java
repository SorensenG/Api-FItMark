package com.Sorensen.FitMark.dto.exercise;

import java.math.BigDecimal;

public record GetExerciseDetailsResponse(String name,
                                         String workoutName,
                                         Integer sets,
                                         BigDecimal maxWeight,
                                         Integer lastTopReps,
                                         Integer positionInWorkout) {
}
