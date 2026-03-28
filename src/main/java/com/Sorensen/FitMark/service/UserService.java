package com.Sorensen.FitMark.service;

import com.Sorensen.FitMark.config.security.JWT.TokenConfig;
import com.Sorensen.FitMark.dto.auth.LoginResponse;
import com.Sorensen.FitMark.dto.auth.RegisterUserRequest;
import com.Sorensen.FitMark.dto.auth.RegisterUserResponse;
import com.Sorensen.FitMark.dto.auth.UserDetailsResponse;
import com.Sorensen.FitMark.entity.RefreshToken;
import com.Sorensen.FitMark.entity.User;
import com.Sorensen.FitMark.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;



    public UserService(AuthenticationManager authenticationManager, TokenConfig tokenConfig, PasswordEncoder passwordEncoder, UserRepository userRepository, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenConfig = tokenConfig;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
    }


    public LoginResponse login(String email, String password) {

        UsernamePasswordAuthenticationToken userAndPassword = new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManager.authenticate(userAndPassword);

        User user = (User) authentication.getPrincipal();
        String accessToken = tokenConfig.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new LoginResponse(accessToken, refreshToken.getToken());
    }


    public RegisterUserResponse registerUser(RegisterUserRequest request){

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        userRepository.save(user);

        return new RegisterUserResponse(user.getUsername(), user.getEmail());
    }

    public UserDetailsResponse getUserDetails(UUID userId) {

        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
       Optional <User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        return new UserDetailsResponse(user.get().getId(), user.get().getUsername(), user.get().getEmail(),user.get().getCreatedAt());
    }
}

