package com.Sorensen.FitMark.dto.workout;

import com.Sorensen.FitMark.entity.SetType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateSetLogRequest(
        @NotNull @Min(0) Integer reps,
        BigDecimal weight,
        @NotNull SetType setType,
        Integer restSeconds,
        String customLabel
) {}
