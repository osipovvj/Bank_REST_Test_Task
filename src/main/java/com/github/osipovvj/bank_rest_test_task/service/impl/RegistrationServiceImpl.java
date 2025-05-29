package com.github.osipovvj.bank_rest_test_task.service.impl;

import com.github.osipovvj.bank_rest_test_task.dto.UserDto;
import com.github.osipovvj.bank_rest_test_task.dto.request.RegistrationRequest;
import com.github.osipovvj.bank_rest_test_task.entity.User;
import com.github.osipovvj.bank_rest_test_task.enums.Role;
import com.github.osipovvj.bank_rest_test_task.exception.AlreadyExistsException;
import com.github.osipovvj.bank_rest_test_task.repository.UserRepository;
import com.github.osipovvj.bank_rest_test_task.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto registerUser(RegistrationRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new AlreadyExistsException("Пользователь с email: " + request.email() + " уже зарегистрирован.");
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .role(Role.ROLE_USER)
                .build();

        return UserDto.toDto(userRepository.save(user));
    }
}
