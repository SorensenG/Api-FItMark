package com.Sorensen.FitMark.dto.exercise;

import java.util.List;
import java.util.UUID;

public record ReorderExercisesRequest(List<ExercisePosition> exercises) {
    public record ExercisePosition(UUID id, int position) {}
}
