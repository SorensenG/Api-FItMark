package com.Sorensen.FitMark.dto.workout;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ListAllSessionsResponse(UUID id, String workoutName, String notas, Boolean status, Integer durationMinutes, OffsetDateTime createdAt, String workoutNotes) {
}
