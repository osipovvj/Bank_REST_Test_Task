package com.github.osipovvj.bank_rest_test_task.dto.response;

import com.github.osipovvj.bank_rest_test_task.dto.CardDto;
import com.github.osipovvj.bank_rest_test_task.entity.Card;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(name = "GetAllCardsResponse", description = "Ответ сервера со списком всех карт.")
public record GetCardsResponse(
        @Schema(description = "Общее количество карт.", example = "999")
        Long total,

        @Schema(description = "Количество страниц.", example = "10")
        Integer countOfPage,

        @Schema(description = "Номер страницы.", example = "0")
        Integer page,

        @Schema(description = "Количество карт на странице.", example = "10")
        Integer countOfElements,

        @Schema(description = "Список карт.")
        List<CardDto> cards
) {
    public static GetCardsResponse toResponse(final Page<Card> cards) {
        return new GetCardsResponse(
                cards.getTotalElements(),
                cards.getTotalPages(),
                cards.getNumber(),
                cards.getNumberOfElements(),
                cards.get()
                        .map(CardDto::toDto)
                        .toList()
        );
    }
}
