package com.github.osipovvj.bank_rest_test_task.dto.request;

import com.github.osipovvj.bank_rest_test_task.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

@Schema(name = "UpdateUserRequest", description = "Запрос на изменение пользовательских данных.")
public record UpdateUserRequest(
        @Email(message = "Неверный формат email.")
        @Schema(description = "Новый email пользователя.", example = "johndoe@example.com")
        String email,

        @Schema(description = "Имя пользователя.", example = "John")
        String firstName,

        @Schema(description = "Фамилия пользователя", example = "Doe")
        String lastName,

        @Schema(description = "Роль пользователя в системе.", example = "ROLE_USER")
        Role role
) {
}
