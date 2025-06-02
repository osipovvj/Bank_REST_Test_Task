package com.github.osipovvj.bank_rest_test_task.dto.request;

import com.github.osipovvj.bank_rest_test_task.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.YearMonth;
import java.util.UUID;

@Schema(name = "CardFilterRequest", description = "Параметры фильтрации карт.")
public record CardFilterRequest(
    @Schema(description = "ID владельца карты.", example = "A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11")
    UUID ownerId,

    @Schema(description = "Статус карты.", example = "ACTIVE")
    CardStatus status,

    @Schema(description = "Начало периода выпуска.", example = "2024-01")
    YearMonth issueDateFrom,

    @Schema(description = "Конец периода выпуска.", example = "2024-02")
    YearMonth issueDateTo,

    @Schema(description = "Показывать карты с истекающим сроком (3 месяца).", example = "true")
    Boolean expiringCards,

    @Schema(description = "Сортировать по дате выпуска по убыванию.", example = "false")
    Boolean sortDesc,

    @Schema(description = "Номер страницы. По умолчанию - 0.", example = "0")
    Integer page,

    @Schema(description = "Размер страницы. По умолчанию - 10", example = "10")
    Integer size
) {}