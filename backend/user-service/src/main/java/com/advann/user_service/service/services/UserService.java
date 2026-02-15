package com.advann.user_service.service.services;

import com.advann.user_service.dto.RegisterRequestDto;
import com.advann.user_service.dto.UserResponseDto;

public interface UserService {
    UserResponseDto registerUser(RegisterRequestDto registerRequestDto);
}