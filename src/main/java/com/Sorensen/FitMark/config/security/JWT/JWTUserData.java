package com.Sorensen.FitMark.config.security.JWT;

import lombok.Builder;

import java.util.UUID;
@Builder
public record JWTUserData(UUID uuid, String email) {
}
