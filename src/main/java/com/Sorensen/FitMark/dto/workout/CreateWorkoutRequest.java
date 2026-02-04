package com.Sorensen.FitMark.dto.workout;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CreateWorkoutRequest(
       @NotBlank(message = "Workout must have a title") String title, String notes, Integer position) {}
