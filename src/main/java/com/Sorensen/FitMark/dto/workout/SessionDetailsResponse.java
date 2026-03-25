package com.Sorensen.FitMark.dto.workout;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record SessionDetailsResponse(
        UUID sessionId,
        UUID workoutId,
        String workoutName,
        OffsetDateTime workoutDate,
        Boolean completed,
        Integer durationMinutes,
        String notes,
        List<ExerciseWithSetsResponse> exercises
) {}
