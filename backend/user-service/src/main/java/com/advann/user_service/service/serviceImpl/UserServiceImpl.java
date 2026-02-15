package com.advann.user_service.service.serviceImpl;

import com.advann.user_service.dto.RegisterRequestDto;
import com.advann.user_service.dto.UserResponseDto;
import com.advann.user_service.entity.Role;
import com.advann.user_service.entity.User;
import com.advann.user_service.exception.EmailAlreadyExistsException;
import com.advann.user_service.repository.UserRepository;
import com.advann.user_service.service.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto registerUser(RegisterRequestDto registerRequestDto) {

        if (userRepository.existsByEmail(registerRequestDto.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + registerRequestDto.getEmail());
        }

        User user = User.builder()
                .username(registerRequestDto.getUsername())
                .email(registerRequestDto.getEmail())
                .password(passwordEncoder.encode(registerRequestDto.getPassword()))
                .role(Role.CUSTOMER)
                .build();

        User savedUser = userRepository.save(user);

        return UserResponseDto.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .build();
    }
}