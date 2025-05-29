package com.github.osipovvj.bank_rest_test_task.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AuthenticationResponse", description = "Ответ от сервиса аутентификации")
public record AuthenticationResponse(
        @Schema(description = "JWT токен.", example = "eyJhbGciOiJIUzI1NiJ9...")
        String token
) {
}
