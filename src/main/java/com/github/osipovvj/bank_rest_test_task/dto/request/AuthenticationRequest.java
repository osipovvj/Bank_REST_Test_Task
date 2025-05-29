package com.github.osipovvj.bank_rest_test_task.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "AuthenticationRequest", description = "Запрос на аутентификацию пользователя.")
public record AuthenticationRequest(
        @NotBlank(message = "Поле email обязательно для заполнения.")
        @Email(message = "Неверный формат email.")
        @Schema(description = "Email пользователя.", example = "johndoe@example.com")
        String email,

        @NotBlank(message = "Поле password обязательно для заполнения.")
        @Schema(description = "Пароль пользователя.", example = "123password")
        String password
) {
}
