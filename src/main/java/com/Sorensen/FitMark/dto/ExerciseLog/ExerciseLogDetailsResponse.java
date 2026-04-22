package com.Sorensen.FitMark.dto.ExerciseLog;


import com.Sorensen.FitMark.entity.SetType;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public record ExerciseLogDetailsResponse(
        UUID exerciseId,
        String exerciseName,
        Integer setNumber,
        Integer reps,
        BigDecimal weight,
        Integer restSeconds,
        Integer lastTopSetReps,
        Date realizedAt,
        SetType setType,
        String customLabel
) {
};
