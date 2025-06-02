package com.github.osipovvj.bank_rest_test_task.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(name = "TransferRequest", description = "Запрос не перевод между картами.")
public record TransferRequest(
        @Schema(description = "ID карты перевода.", example = "A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11")
        UUID fromCard,

        @Schema(description = "ID карты назначения.", example = "A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A12")
        UUID toCard,

        @Schema(description = "Сумма перевода.", example = "100.0")
        BigDecimal amount
) {
}
