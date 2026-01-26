package com.Sorensen.FitMark.controller;

import com.Sorensen.FitMark.config.TokenConfig;
import com.Sorensen.FitMark.dto.request.LoginRequest;
import com.Sorensen.FitMark.dto.request.RegisterUserRequest;
import com.Sorensen.FitMark.dto.response.LoginResponse;
import com.Sorensen.FitMark.dto.response.RegisterUserResponse;
import com.Sorensen.FitMark.entity.User;
import com.Sorensen.FitMark.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class AuthController {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, TokenConfig tokenConfig) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenConfig = tokenConfig;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest login) {

        UsernamePasswordAuthenticationToken userAndPassword = new UsernamePasswordAuthenticationToken(login.email(), login.password());
        Authentication authentication = authenticationManager.authenticate(userAndPassword);

        User user = (User) authentication.getPrincipal();
        String token = tokenConfig.generateToken(user);
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
       user.setPasswordHash(passwordEncoder.encode(request.password()));

        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterUserResponse(user.getUsername(), user.getEmail()));
    }
}




