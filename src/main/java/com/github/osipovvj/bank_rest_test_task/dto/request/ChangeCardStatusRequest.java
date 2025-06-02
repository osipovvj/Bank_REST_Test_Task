package com.github.osipovvj.bank_rest_test_task.dto.request;

import com.github.osipovvj.bank_rest_test_task.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "UpdateCardDataRequest", description = "Запрос на изменение данных карты.")
public record ChangeCardStatusRequest(
        @NotNull(message = "Поле 'Card Status' не должно быть нуль.")
        @Schema(description = "Статус карты.", example = "BLOCKED")
        CardStatus status
) {
}
