package com.Sorensen.FitMark.dto.split;

import com.Sorensen.FitMark.dto.exercise.ExerciseSessionResponse;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record SplitDetailsResponse(
        UUID id,
        String name,
        OffsetDateTime createdAt,
        List<WorkoutSummary> workouts
) {
    public record WorkoutSummary(
            UUID id,
            String title,
            Integer position,
            List<ExerciseSessionResponse> exercises
    ) {}
}
