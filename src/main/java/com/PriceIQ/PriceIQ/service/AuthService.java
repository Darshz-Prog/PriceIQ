package com.PriceIQ.PriceIQ.service;

import com.PriceIQ.PriceIQ.dto.request.LoginRequest;
import com.PriceIQ.PriceIQ.dto.request.RegisterRequest;
import com.PriceIQ.PriceIQ.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(String refreshToken);
}