package com.advann.user_service.service.services;

import com.advann.user_service.dto.AuthResponseDto;
import com.advann.user_service.dto.LoginRequestDto;
import com.advann.user_service.dto.RegisterRequestDto;

public interface AuthService {

    void register(RegisterRequestDto registerRequestDto);

    AuthResponseDto login(LoginRequestDto loginRequestDto);

    AuthResponseDto refreshAccessToken(String refreshToken);

    void logout(String refreshToken, String accessToken);
}