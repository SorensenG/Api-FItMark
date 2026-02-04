package com.Sorensen.FitMark.config.security.service;

import com.Sorensen.FitMark.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthConfigService implements UserDetailsService {


    private final UserRepository userRepository;

    public AuthConfigService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(username).orElseThrow(()-> new UsernameNotFoundException("User not found with email: " + username));
    }
}
