package com.Sorensen.FitMark.dto.workout;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ActiveSessionResponse(UUID sessionId, UUID workoutId, UUID splitId, String workoutTitle, OffsetDateTime startedAt) {
}
