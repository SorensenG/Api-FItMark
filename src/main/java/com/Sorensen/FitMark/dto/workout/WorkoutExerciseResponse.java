package com.Sorensen.FitMark.dto.workout;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record WorkoutExerciseResponse(
        UUID id,
        String name,
        Integer sets,
        Integer lastTopSetReps,
        BigDecimal weight,
        OffsetDateTime createdAt,
        Integer position
) {}
