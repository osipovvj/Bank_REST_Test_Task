package com.github.osipovvj.bank_rest_test_task.service.impl;

import com.github.osipovvj.bank_rest_test_task.dto.request.AuthenticationRequest;
import com.github.osipovvj.bank_rest_test_task.dto.response.AuthenticationResponse;
import com.github.osipovvj.bank_rest_test_task.entity.User;
import com.github.osipovvj.bank_rest_test_task.exception.ResourceNotFoundException;
import com.github.osipovvj.bank_rest_test_task.repository.UserRepository;
import com.github.osipovvj.bank_rest_test_task.security.service.JwtService;
import com.github.osipovvj.bank_rest_test_task.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional(readOnly = true)
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с email " + request.email() + " не найден."));
        String token = jwtService.generateToken(user);

        return new AuthenticationResponse(token);
    }
}
