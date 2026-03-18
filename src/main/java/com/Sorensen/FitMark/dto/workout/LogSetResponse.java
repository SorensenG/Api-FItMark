package com.Sorensen.FitMark.dto.workout;

import com.Sorensen.FitMark.entity.SetType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record LogSetResponse(
        UUID setLogId,
        UUID sessionId,
        UUID exerciseId,
        Integer setNumber,
        Integer reps,
        SetType setType,
        BigDecimal weight,
        Integer restSeconds,
        OffsetDateTime createdAt
) {}
