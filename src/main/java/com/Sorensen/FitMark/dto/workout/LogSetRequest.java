package com.Sorensen.FitMark.dto.workout;

import com.Sorensen.FitMark.entity.SetType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record LogSetRequest(
        @NotNull UUID exerciseId,
        @NotNull @Min(1) Integer setNumber,
        @NotNull @Min(0) Integer reps,
        @NotNull SetType setType,
        BigDecimal weight,
        Integer restSeconds
) {}
