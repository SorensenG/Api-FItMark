package com.Sorensen.FitMark.dto.workout;

import com.Sorensen.FitMark.entity.Exercise;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record StartWorkOutSessionResponse(
        UUID sessionId,
        UUID workoutId,
        String workoutTitle,
        OffsetDateTime startedAt,
        Boolean completed,
        List<Exercise> workoutExercises
) {}