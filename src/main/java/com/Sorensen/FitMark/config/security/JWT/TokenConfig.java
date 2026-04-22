package com.Sorensen.FitMark.config.security.JWT;


import com.Sorensen.FitMark.entity.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Component
public class TokenConfig {

    @Value("${jwt.secret}")
    private String secret;

    @PostConstruct
    public void validateSecretStrength() {
        if (secret == null || secret.trim().length() < 32) {
            throw new IllegalStateException("JWT_SECRET must have at least 32 characters");
        }
    }


public String generateToken(User user) {
    return JWT.create()
            .withSubject(user.getEmail())
            .withClaim("id", user.getId().toString()).withExpiresAt(Date.from(Instant.now().plus(30, ChronoUnit.MINUTES)))
            .withIssuedAt(Date.from(Instant.now()))
            .sign(com.auth0.jwt.algorithms.Algorithm.HMAC256(secret));


}


   public Optional<JWTUserData> validateToken(String token) {

  try {
    DecodedJWT jwt = JWT.require(com.auth0.jwt.algorithms.Algorithm.HMAC256(secret))
              .build()
               .verify(token);

       return Optional.of(JWTUserData.builder()
               .email(jwt.getSubject())
              .uuid(java.util.UUID.fromString(jwt.getClaim("id").asString()))
               .build());
   }catch (JWTVerificationException e){
       return Optional.empty();
    }

}
}