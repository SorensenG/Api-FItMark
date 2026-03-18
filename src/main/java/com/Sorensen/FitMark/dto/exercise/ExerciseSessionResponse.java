package com.Sorensen.FitMark.dto.exercise;

import java.math.BigDecimal;
import java.util.UUID;

public record ExerciseSessionResponse(
        UUID id,
        String name,
        Integer sets,
        Integer lastTopSetReps,
        BigDecimal weight,
        Integer position
) {}
