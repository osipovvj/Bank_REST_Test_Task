package com.github.osipovvj.bank_rest_test_task.service.impl;

import com.github.osipovvj.bank_rest_test_task.dto.UserDto;
import com.github.osipovvj.bank_rest_test_task.dto.request.UpdateUserRequest;
import com.github.osipovvj.bank_rest_test_task.dto.response.GetAllUsersResponse;
import com.github.osipovvj.bank_rest_test_task.entity.User;
import com.github.osipovvj.bank_rest_test_task.exception.AlreadyExistsException;
import com.github.osipovvj.bank_rest_test_task.exception.ResourceNotFoundException;
import com.github.osipovvj.bank_rest_test_task.repository.UserRepository;
import com.github.osipovvj.bank_rest_test_task.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public GetAllUsersResponse getAllUsers(int offset, int limit) {
        return GetAllUsersResponse.toResponse(
                userRepository.findAllByOrderByCreatedAtAsc(PageRequest.of(offset, limit))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользоваетль с ID " + id + " не найден."));

        return UserDto.toDto(user);
    }

    @Override
    public UserDto updateUser(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользоваетль с ID " + id + " не найден."));

        if (!user.getEmail().equals(request.email())) {
            if (userRepository.existsByEmailAndIdNot(request.email(), id)) {
                throw new AlreadyExistsException("Пользователь с email " + request.email() + " уже зарегестрирован.");
            } else {
                user.setEmail(request.email());
            }
        }
        if (!user.getFirstName().equals(request.firstName())) user.setFirstName(request.firstName());
        if (!user.getLastName().equals(request.lastName())) user.setLastName(request.lastName());
        if (!user.getRole().equals(request.role())) user.setRole(request.role());

        return UserDto.toDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Пользоваетль с ID " + id + " не найден.");
        }

        userRepository.deleteById(id);
    }
}
