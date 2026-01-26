package com.Sorensen.FitMark.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.util.Optionals;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private TokenConfig tokenConfig;

    public SecurityFilter(TokenConfig tokenConfig) {
        this.tokenConfig = tokenConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (Strings.isNotEmpty(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring("Bearer ".length());
            Optional<JWTUserData> jwtUserData = tokenConfig.validateToken(token);

            if (jwtUserData.isPresent()) {
                JWTUserData userData = jwtUserData.get();
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userData,
                        null,
                        null
                );

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            }

            }
        filterChain.doFilter(request, response);

    }
    }

