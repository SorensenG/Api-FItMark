package com.Sorensen.FitMark.dto.workout;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CreateWorkoutRequest(
       @NotBlank String title,
        @Nullable String notes,@Nullable OffsetDateTime date) {}
