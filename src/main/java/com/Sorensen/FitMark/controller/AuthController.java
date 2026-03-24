package com.Sorensen.FitMark.controller;

import com.Sorensen.FitMark.dto.auth.*;
import com.Sorensen.FitMark.entity.User;
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




