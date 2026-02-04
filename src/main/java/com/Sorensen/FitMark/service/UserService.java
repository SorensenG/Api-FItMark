package com.Sorensen.FitMark.service;

import com.Sorensen.FitMark.config.security.JWT.TokenConfig;
import com.Sorensen.FitMark.dto.auth.RegisterUserRequest;
import com.Sorensen.FitMark.dto.auth.RegisterUserResponse;
import com.Sorensen.FitMark.dto.user.UserDetailsResponse;
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



    public UserService(AuthenticationManager authenticationManager, TokenConfig tokenConfig, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenConfig = tokenConfig;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }


    public String login(String email, String password) {

        UsernamePasswordAuthenticationToken userAndPassword = new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManager.authenticate(userAndPassword);

        User user = (User) authentication.getPrincipal();
        String token = tokenConfig.generateToken(user);


        return token;
    }


    public RegisterUserResponse registerUser(RegisterUserRequest request){

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        userRepository.save(user);

        return new RegisterUserResponse(user.getUsername(), user.getEmail());
    }

    public UserDetailsResponse getUserDetails(UUID id) {

        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            User present = user.get();
            return new UserDetailsResponse(present.getUsername(),present.getId(), present.getEmail(), present.getCreatedAt());
        } else {
            throw new IllegalArgumentException("User not found");
        }

    }
}

