package com.github.osipovvj.bank_rest_test_task.service;

import com.github.osipovvj.bank_rest_test_task.dto.UserDto;
import com.github.osipovvj.bank_rest_test_task.dto.request.UpdateUserRequest;
import com.github.osipovvj.bank_rest_test_task.dto.response.GetAllUsersResponse;

import java.util.UUID;

public interface UserService {
    GetAllUsersResponse getAllUsers(int offset, int limit);
    UserDto getUserById(UUID id);
    UserDto updateUser(UUID id, UpdateUserRequest request);
    void deleteUser(UUID id);
}
