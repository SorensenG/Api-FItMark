package com.Sorensen.FitMark.dto.workout;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ExerciseWithSetsResponse(
        UUID exerciseId,
        String exerciseName,
        Integer plannedSets,
        Integer lastTopSetReps,
        BigDecimal weight,
        Integer position,
        List<SetLogDetails> sets
) {}
