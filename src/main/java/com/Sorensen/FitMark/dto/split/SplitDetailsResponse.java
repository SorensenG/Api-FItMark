package com.Sorensen.FitMark.dto.split;

import com.Sorensen.FitMark.dto.workout.StartWorkOutSessionResponse;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record SplitDetailsResponse(
        UUID id,
        String name,
        OffsetDateTime createdAt,
        List<StartWorkOutSessionResponse> workouts
) {}