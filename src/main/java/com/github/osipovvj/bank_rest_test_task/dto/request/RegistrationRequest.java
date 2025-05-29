package com.github.osipovvj.bank_rest_test_task.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "RegistrationRequest", description = "Запрос на регистрацию нового пользователя.")
public record RegistrationRequest(
        @NotBlank(message = "Поле 'email' обязательно для заполнения.")
        @Email(message = "Неверный формат email.")
        @Schema(description = "Email пользователя.", example = "johndoe@example.com")
        String email,

        @NotBlank(message = "Поле 'password' обязательно для заполнения.")
        @Size(min = 8, message = "Пароль должен быть не менее 8 символов.")
        @Schema(description = "Пароль пользователя.", example = "123password")
        String password,

        @NotBlank(message = "Поле 'Имя' обязательно для заполнения.")
        @Schema(description = "Имя пользователя", example = "John")
        String firstName,

        @NotBlank(message = "Поле 'Фамилия' обязательно для заполнения.")
        @Schema(description = "Фамилия пользователя", example = "Doe")
        String lastName
) {
}
