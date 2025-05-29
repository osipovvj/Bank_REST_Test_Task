package com.github.osipovvj.bank_rest_test_task.dto;

import com.github.osipovvj.bank_rest_test_task.entity.User;
import com.github.osipovvj.bank_rest_test_task.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(name = "UserDto", description = "Ответ сервера с информацией о пользователе.")
public record UserDto(
        @Schema(description = "ID пользователя.", example = "A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11")
        UUID id,

        @Schema(description = "Имя пользователя.", example = "John")
        String firstName,

        @Schema(description = "Фамилия пользователя.", example = "Doe")
        String lastName,

        @Schema(description = "Email пользователя.", example = "johndoe@example.com")
        String email,

        @Schema(description = "Роль пользователя в системе.", example = "ROLE_USER")
        Role role,

        @Schema(description = "Дата и время регистрации пользователя.", example = "2025-05-07T23:18:16")
        LocalDateTime createdAt,

        @Schema(description = "Дата и время изменения данных пользователя.", example = "2025-05-07T23:18:16")
        LocalDateTime updatedAt
) {
    public static UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
