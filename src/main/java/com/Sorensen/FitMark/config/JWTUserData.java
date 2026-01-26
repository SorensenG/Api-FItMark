package com.Sorensen.FitMark.config;

import lombok.Builder;

import java.util.UUID;
@Builder
public record JWTUserData(UUID uuid, String email) {
}
