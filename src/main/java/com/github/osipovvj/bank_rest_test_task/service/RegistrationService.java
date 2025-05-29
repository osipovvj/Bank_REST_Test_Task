package com.github.osipovvj.bank_rest_test_task.service;

import com.github.osipovvj.bank_rest_test_task.dto.UserDto;
import com.github.osipovvj.bank_rest_test_task.dto.request.RegistrationRequest;

public interface RegistrationService {
    UserDto registerUser(RegistrationRequest request);
}
