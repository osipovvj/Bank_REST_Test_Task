package com.github.osipovvj.bank_rest_test_task.dto.request;

import com.github.osipovvj.bank_rest_test_task.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(name = "UpdateUserRequest", description = "Запрос на изменение пользовательских данных.")
public record UpdateUserRequest(
        @NotBlank(message = "Поле 'email' обязательно для заполнения.")
        @Email(message = "Неверный формат email.")
        @Schema(description = "Новый email пользователя.", example = "johndoe@example.com")
        String email,

        @NotBlank(message = "Поле 'Имя' обязательно для заполнения.")
        @Schema(description = "Имя пользователя.", example = "John")
        String firstName,

        @NotBlank(message = "Поле 'Фамилия' обязательно для заполнения.")
        @Schema(description = "Фамилия пользователя", example = "Doe")
        String lastName,

        @NotNull(message = "Поле 'Role' не должно быть null.")
        @Schema(description = "Роль пользователя в системе.", example = "ROLE_USER")
        Role role
) {
}
