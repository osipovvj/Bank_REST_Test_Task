package com.github.osipovvj.bank_rest_test_task.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(name = "CreateCardRequest", description = "Запрос на создание новой карты.")
public record CreateCardRequest(
        @NotNull(message = "Поле 'Cardholder ID' не должно быть null.")
        @Schema(description = "ID будущего держателя карты.", example = "A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11")
        UUID cardholderId
) {
}
