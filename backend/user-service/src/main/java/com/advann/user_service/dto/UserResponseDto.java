package com.advann.user_service.dto;

import com.advann.user_service.entity.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private Role role;
}