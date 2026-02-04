package com.Sorensen.FitMark.controller;

import com.Sorensen.FitMark.dto.auth.LoginRequest;
import com.Sorensen.FitMark.dto.auth.RegisterUserRequest;
import com.Sorensen.FitMark.dto.auth.LoginResponse;
import com.Sorensen.FitMark.dto.auth.RegisterUserResponse;
import com.Sorensen.FitMark.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class AuthController {


    private final UserService service;

    public AuthController(UserService service) {
        this.service = service;
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest login) {

        var token = service.login(login.email(), login.password());

        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@Valid @RequestBody RegisterUserRequest request) {

        var user = service.registerUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterUserResponse(user.username(), user.email()));
    }
}




