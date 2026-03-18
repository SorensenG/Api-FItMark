package com.Sorensen.FitMark.dto.workout;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record FinishSessionResponse(
        UUID sessionId,
        UUID workoutId,
        String workoutTitle,
        OffsetDateTime startedAt,
        Boolean completed,
        Integer durationMinutes,
        String notes,
        List<ExerciseSummary> exercises,
        BigDecimal totalVolumeKg
) {}
