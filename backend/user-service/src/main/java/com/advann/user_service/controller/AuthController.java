package com.advann.user_service.controller;

import com.advann.user_service.dto.AuthResponseDto;
import com.advann.user_service.dto.LoginRequestDto;
import com.advann.user_service.dto.RefreshTokenRequestDto;
import com.advann.user_service.dto.RegisterRequestDto;
import com.advann.user_service.payload.ApiResponse;
import com.advann.user_service.service.services.AuthService;
import com.advann.user_service.service.services.TokenBlacklistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequestDto registerRequestDto) {

        authService.register(registerRequestDto);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("User registered successfully")
                .data("Registration successful")
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {

        AuthResponseDto authResponse = authService.login(loginRequestDto);

        ApiResponse<AuthResponseDto> response = ApiResponse.<AuthResponseDto>builder()
                .success(true)
                .message("Login successful")
                .data(authResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponseDto>> refreshToken(
            @Valid @RequestBody RefreshTokenRequestDto requestDto) {

        AuthResponseDto responseDto = authService.refreshAccessToken(requestDto.getRefreshToken());

        ApiResponse<AuthResponseDto> response = ApiResponse.<AuthResponseDto>builder()
                .success(true)
                .message("Token refreshed successfully")
                .data(responseDto)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody RefreshTokenRequestDto requestDto) {

        String accessToken = authHeader.substring(7);

        authService.logout(requestDto.getRefreshToken(), accessToken);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Logout successful")
                .data("Refresh token revoked + Access token blacklisted")
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate-token")
    public ResponseEntity<ApiResponse<String>> validateToken(
            @RequestHeader("Authorization") String authHeader
    ) {

        String token = authHeader.substring(7);

        boolean isBlacklisted = tokenBlacklistService.isBlacklisted(token);

        if (isBlacklisted) {
            ApiResponse<String> response = ApiResponse.<String>builder()
                    .success(false)
                    .message("Token is blacklisted")
                    .data(null)
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Token is valid")
                .data("VALID")
                .build();

        return ResponseEntity.ok(response);
    }
}