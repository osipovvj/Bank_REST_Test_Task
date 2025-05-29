package com.github.osipovvj.bank_rest_test_task.service.impl;

import com.github.osipovvj.bank_rest_test_task.repository.UserRepository;
import com.github.osipovvj.bank_rest_test_task.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
}
