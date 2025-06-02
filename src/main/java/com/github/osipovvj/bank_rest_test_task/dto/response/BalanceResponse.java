package com.github.osipovvj.bank_rest_test_task.dto.response;

import com.github.osipovvj.bank_rest_test_task.entity.Card;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "BalanceResponse", description = "Ответ сервера с номером карты и балансом по ней.")
public record BalanceResponse(
        @Schema(description = "Номер карты.", example = "**** **** **** 1234")
        String cardNumber,

        @Schema(description = "Баланс по карте.", example = "100.0")
        BigDecimal balance
) {
    public static BalanceResponse toResponse(Card card) {
        return new BalanceResponse(card.getMaskedCardNumber(), card.getBalance());
    }
}
