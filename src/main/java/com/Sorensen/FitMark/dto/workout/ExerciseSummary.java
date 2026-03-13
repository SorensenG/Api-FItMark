package com.Sorensen.FitMark.dto.workout;

import java.math.BigDecimal;

public record ExerciseSummary(
        String name,
        Integer totalSets,
        Integer totalReps,
        BigDecimal totalVolume,     // soma de (peso * reps) por série
        BigDecimal topSetWeight,    // peso da série mais pesada (peso * reps)
        Integer topSetReps,         // repetições da série mais pesada
        BigDecimal topSetVolume     // peso * reps da série mais pesada
) {}
