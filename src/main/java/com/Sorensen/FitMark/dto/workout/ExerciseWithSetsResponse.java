package com.Sorensen.FitMark.dto.workout;

import java.util.List;
import java.util.UUID;

public record ExerciseWithSetsResponse(
        UUID exerciseId,
        String exerciseName,
        List<SetLogDetails> sets
) {}
