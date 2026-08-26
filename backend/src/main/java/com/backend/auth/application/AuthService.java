package com.backend.auth.application;

import com.backend.auth.api.LoginRequest;
import com.backend.auth.api.AuthResponse;
import com.backend.auth.api.RegisterRequest;
import com.backend.auth.infrastructure.JwtService;

import com.backend.common.exception.InvalidCredentialsException;
import com.backend.common.exception.ResourceAlreadyExistsException;
import com.backend.user.domain.User;
import com.backend.user.infrastructure.UserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new ResourceAlreadyExistsException(
                    "Invalid email or password."
            );
        }

        String passwordHash = passwordEncoder.encode(request.password());

        User user = new User(
                request.email(),
                request.displayName(),
                passwordHash
        );

        userRepository.save(user);

        Authentication authentication
                = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                user.email(),
                                request.password()
                        )
                );

        String token = jwtService.generateToken(authentication);

        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {

        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.email(),
                                    request.password()
                            )
                    );

            String token = jwtService.generateToken(authentication);

            return new AuthResponse(token);

        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException(
                    "Wrong email or password."
            );
        }
    }
}
