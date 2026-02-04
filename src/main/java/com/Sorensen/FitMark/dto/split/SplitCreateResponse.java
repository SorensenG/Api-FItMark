package com.Sorensen.FitMark.dto.split;

import java.util.UUID;

public record SplitCreateResponse(UUID splitId, UUID userId, String title) {
}
