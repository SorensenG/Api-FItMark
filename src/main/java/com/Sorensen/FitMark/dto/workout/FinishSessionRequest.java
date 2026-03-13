package com.Sorensen.FitMark.dto.workout;

import jakarta.validation.constraints.NotNull;

public record FinishSessionRequest(
        @NotNull Integer durationMinutes,
        String notes
) {}
