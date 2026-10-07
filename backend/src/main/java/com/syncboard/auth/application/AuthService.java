package com.syncboard.auth.application;

import com.syncboard.auth.api.LoginRequest;
import com.syncboard.auth.api.RegisterRequest;
import com.syncboard.auth.infrastructure.JwtService;

import com.syncboard.common.exception.InvalidCredentialsException;
import com.syncboard.common.exception.ResourceAlreadyExistsException;
import com.syncboard.user.domain.User;
import com.syncboard.user.infrastructure.UserRepository;

import jakarta.servlet.http.HttpServletResponse;
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
    private final AuthCookieService authCookieService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthCookieService authCookieService,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authCookieService = authCookieService;
        this.authenticationManager = authenticationManager;
    }

    public void register(RegisterRequest request, HttpServletResponse response) {

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

        authCookieService.setAuthCookie(response, token);
    }

    public void login(LoginRequest request, HttpServletResponse response) {

        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.email(),
                                    request.password()
                            )
                    );

            String token = jwtService.generateToken(authentication);

            authCookieService.setAuthCookie(response, token);

        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException(
                    "Wrong email or password."
            );
        }
    }

    public void logout(HttpServletResponse response) {
        authCookieService.clearAuthCookie(response);
    }
}
