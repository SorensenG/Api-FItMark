package com.Sorensen.FitMark.dto.workout;

import java.util.UUID;

public record AbandonSessionResponse(UUID sessionId, Boolean abandoned) {}
