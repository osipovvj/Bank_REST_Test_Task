package com.github.osipovvj.bank_rest_test_task.exception.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(name = "ProblemDetailResponse", description = "Ответ сервера с информацией о возникшей ошибке.")
public record ProblemDetailResponse(
        @Schema(description = "Время возникновения ошибки.", example = "2025-05-15T21:00:00Z")
        LocalDateTime timestamp,

        @Schema(description = "Идентификатор ошибки.", example = "/errors/...")
        String type,

        @Schema(description = "Название ошибки.", example = "... Error")
        String title,

        @Schema(description = "HTTP статус-код.", example = "40..")
        Integer status,

        @Schema(description = "Описание ошибки.", example = "Пользователь с ...")
        String detail,

        @Schema(description = "Ендпоинт возниковения ошибки.", example = "/api/v1/...")
        String instance,

        @Schema(description = "Список возникших ошибок.")
        List<FieldErrorDetail> errors
) {

    @Schema(name = "FieldErrorDetail", description = "Детали ошибок по конкретному полю.")
    public record FieldErrorDetail(
            @Schema(description = "Наименование свойства в котором возникла ошибка.", example = "email/name/..")
            String field,

            @Schema(description = "Сообщение об ошибке.", example = "Некорректные данные.")
            String message
    ) {}
}
