package com.advann.user_service.service.services;

import com.advann.user_service.entity.RefreshToken;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(String email);

    RefreshToken verifyExpiration(RefreshToken token);

    void revokeToken(String token);
}