package com.advann.user_service.controller;

import com.advann.user_service.payload.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<String>> getProfile() {

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Profile fetched successfully")
                .data("This is protected user profile API")
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public String userTest() {
        return "Hello User, You are authorized!";
    }
}