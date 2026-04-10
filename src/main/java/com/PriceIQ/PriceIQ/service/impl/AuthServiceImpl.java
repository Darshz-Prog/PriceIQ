package com.PriceIQ.PriceIQ.service.impl;

import com.PriceIQ.PriceIQ.config.JwtService;
import com.PriceIQ.PriceIQ.dto.request.LoginRequest;
import com.PriceIQ.PriceIQ.dto.request.RegisterRequest;
import com.PriceIQ.PriceIQ.dto.response.AuthResponse;
import com.PriceIQ.PriceIQ.entity.Role;
import com.PriceIQ.PriceIQ.entity.User;
import com.PriceIQ.PriceIQ.exception.InvalidRequestException;
import com.PriceIQ.PriceIQ.repository.UserRepository;
import com.PriceIQ.PriceIQ.security.CustomUserDetailsService;
import com.PriceIQ.PriceIQ.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new InvalidRequestException("Email already exists");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .enabled(true)
                .accountNonLocked(true)
                .build();

        userRepository.save(user);

        UserDetails userdetail_user = customUserDetailsService.loadUserByUsername(request.getEmail());

        String accessToken = jwtService.generateToken(userdetail_user);
        String refreshToken = jwtService.generateRefreshToken(userdetail_user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .email(user.getEmail())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidRequestException("Invalid credentials"));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());

        return AuthResponse.builder()
                .accessToken(jwtService.generateToken(userDetails))
                .refreshToken(jwtService.generateRefreshToken(userDetails))
                .userId(user.getId())
                .email(user.getEmail())
                .build();
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {

        String token = refreshToken.replace("Bearer ", "");
        String email = jwtService.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());

        return AuthResponse.builder()
                .accessToken(jwtService.generateToken(userDetails))
                .refreshToken(token)
                .userId(user.getId())
                .email(user.getEmail())
                .build();
    }
}