package com.Sorensen.FitMark.controller;

import com.Sorensen.FitMark.config.security.JWT.TokenConfig;
import com.Sorensen.FitMark.dto.auth.*;
import com.Sorensen.FitMark.entity.RefreshToken;
import com.Sorensen.FitMark.entity.User;
import com.Sorensen.FitMark.service.RefreshTokenService;
import com.Sorensen.FitMark.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {


    private final UserService service;
    private final RefreshTokenService refreshTokenService;
    private final TokenConfig tokenConfig;

    public AuthController(UserService service, RefreshTokenService refreshTokenService, TokenConfig tokenConfig) {
        this.service = service;
        this.refreshTokenService = refreshTokenService;
        this.tokenConfig = tokenConfig;
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest login) {

        var loginResponse = service.login(login.email(), login.password());

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {

        RefreshToken validated = refreshTokenService.validateRefreshToken(request.refreshToken());
        refreshTokenService.revokeRefreshToken(request.refreshToken());

        User user = validated.getUser();
        String newAccessToken = tokenConfig.generateToken(user);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

        return ResponseEntity.ok(new LoginResponse(newAccessToken, newRefreshToken.getToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request, @AuthenticationPrincipal User user) {

        refreshTokenService.revokeRefreshToken(request.refreshToken());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@Valid @RequestBody RegisterUserRequest request) {

        var user = service.registerUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterUserResponse(user.username(), user.email()));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDetailsResponse> getUser(@AuthenticationPrincipal User user ){
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var userId=user.getId();
         var userDetails = service.getUserDetails(userId);

         return ResponseEntity.status(HttpStatus.OK).body(userDetails);
    }
}




