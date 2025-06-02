package com.github.osipovvj.bank_rest_test_task.service;

import com.github.osipovvj.bank_rest_test_task.dto.UserDto;
import com.github.osipovvj.bank_rest_test_task.dto.request.UpdateUserRequest;
import com.github.osipovvj.bank_rest_test_task.dto.response.GetAllUsersResponse;
import com.github.osipovvj.bank_rest_test_task.entity.User;
import com.github.osipovvj.bank_rest_test_task.enums.Role;
import com.github.osipovvj.bank_rest_test_task.exception.AlreadyExistsException;
import com.github.osipovvj.bank_rest_test_task.exception.ResourceNotFoundException;
import com.github.osipovvj.bank_rest_test_task.repository.UserRepository;
import com.github.osipovvj.bank_rest_test_task.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    void getAllUsers_ShouldReturnPagedResponse() {
        int offset = 0;
        int limit = 10;
        List<User> users = List.of(testUser);
        Page<User> page = new PageImpl<>(users);
        when(userRepository.findAllByOrderByCreatedAtAsc(any(PageRequest.class))).thenReturn(page);

        GetAllUsersResponse response = userService.getAllUsers(offset, limit);

        assertThat(response.users()).hasSize(1);
        assertThat(response.total()).isEqualTo(1);
        assertThat(response.page()).isZero();
        verify(userRepository).findAllByOrderByCreatedAtAsc(any(PageRequest.class));
    }

    @Test
    void getAllUsers_WhenEmpty_ShouldReturnEmptyResponse() {
        when(userRepository.findAllByOrderByCreatedAtAsc(any(PageRequest.class)))
                .thenReturn(Page.empty());

        GetAllUsersResponse response = userService.getAllUsers(0, 10);

        assertThat(response.users()).isEmpty();
        assertThat(response.total()).isZero();
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUserDto() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        UserDto result = userService.getUserById(userId);

        assertThat(result.id()).isEqualTo(userId);
        assertThat(result.email()).isEqualTo(testUser.getEmail());
        verify(userRepository).findById(userId);
    }

    @Test
    void getUserById_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(userId.toString());
    }

    @Test
    void updateUser_WhenValidUpdate_ShouldUpdateUser() {
        UpdateUserRequest request = new UpdateUserRequest(
                "new@example.com",
                "NewJohn",
                "NewDoe",
                Role.ROLE_USER
        );
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmailAndIdNot(request.email(), userId)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDto result = userService.updateUser(userId, request);

        assertThat(result).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_WhenEmailAlreadyExists_ShouldThrowException() {
        UpdateUserRequest request = new UpdateUserRequest(
                "existing@example.com",
                "John",
                "Doe",
                Role.ROLE_USER
        );
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmailAndIdNot(request.email(), userId)).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser(userId, request))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessageContaining(request.email());
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDeleteUser() {
        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(userId.toString());
    }
}
