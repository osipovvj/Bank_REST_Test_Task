package com.github.osipovvj.bank_rest_test_task.dto;

import com.github.osipovvj.bank_rest_test_task.entity.Card;
import com.github.osipovvj.bank_rest_test_task.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.YearMonth;
import java.util.UUID;

@Schema(name = "CardDto", description = "Ответ сервера с информацией о карте.")
public record CardDto(
        @Schema(description = "ID карты.", example = "A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11")
        UUID id,

        @Schema(description = "Номер карты.", example = "**** **** **** 1234")
        String maskedNumber,

        @Schema(description = "Дата выдачи карты.", example = "2025-05")
        YearMonth issueDate,

        @Schema(description = "Дата окончания срока действия.", example = "2025-05")
        YearMonth expirationDate,

        @Schema(description = "Статус карты.", example = "ACTIVE")
        CardStatus status
) {
    public static CardDto toDto(final Card card) {
        return new CardDto(
                card.getId(),
                card.getMaskedCardNumber(),
                card.getIssueDate(),
                card.getExpirationDate(),
                card.getStatus()
        );
    }
}
