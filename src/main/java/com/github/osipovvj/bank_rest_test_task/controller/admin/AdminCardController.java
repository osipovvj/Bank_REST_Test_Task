package com.github.osipovvj.bank_rest_test_task.controller.admin;

import com.github.osipovvj.bank_rest_test_task.dto.CardDto;
import com.github.osipovvj.bank_rest_test_task.dto.request.CardFilterRequest;
import com.github.osipovvj.bank_rest_test_task.dto.request.CreateCardRequest;
import com.github.osipovvj.bank_rest_test_task.dto.request.ChangeCardStatusRequest;
import com.github.osipovvj.bank_rest_test_task.dto.response.GetCardsResponse;
import com.github.osipovvj.bank_rest_test_task.exception.dto.ProblemDetailResponse;
import com.github.osipovvj.bank_rest_test_task.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/cards")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(
        name = "AdminCardController",
        description = "Управление картами (создание, просмотр, изменение, удаление). Только для админов."
)
public class AdminCardController {
    private final CardService cardService;

    @Operation(summary = "Получение списка карт с применением фильтра.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение списка карт.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GetCardsResponse.class)
                    )
            )
    })
    @PostMapping(value = "/filter", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetCardsResponse> cardFilter(
        @RequestBody CardFilterRequest request
    ) {
        return ResponseEntity.ok(cardService.getCards(request));
    }

    @Operation(summary = "Получение данных карты по ID.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение данных карты.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CardDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Карта с указанным ID не найдена.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailResponse.class)
                    )
            )
    })
    @GetMapping(value = "/{card_id}")
    public ResponseEntity<CardDto> getCard(
            @Parameter(description = "ID карты.", example = "A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11")
            @PathVariable UUID card_id
    ) {
        CardDto card = cardService.getCardById(card_id);

        return ResponseEntity.ok(card);
    }

    @Operation(summary = "Создание новой карты.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное создание новой карты.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CardDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверные данные.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailResponse.class)
                    )
            )
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE})
    public ResponseEntity<CardDto> createCard(@Valid @RequestBody CreateCardRequest request) {
        CardDto card = cardService.createCard(request);

        return ResponseEntity.ok(card);
    }

    @Operation(summary = "Изменение статуса карты.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Статус карты успешно изменён.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CardDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Карта с указанным ID не найдена.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailResponse.class)
                    )
            )
    })
    @PostMapping(value = "/{card_id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE})
    public ResponseEntity<CardDto> changeCardStatus(
            @Parameter(description = "ID карты.", example = "A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11")
            @PathVariable UUID card_id,

            @RequestBody ChangeCardStatusRequest request
    ) {
        CardDto card = cardService.updateCard(card_id, request);

        return ResponseEntity.ok(card);
    }

    @Operation(summary = "Удаление карты.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Карты успешно удалена.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Карта с указанным ID не найдена.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailResponse.class)
                    )
            )
    })
    @DeleteMapping(value = "/{card_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE})
    public ResponseEntity<Void> deleteCard(
            @Parameter(description = "ID карты.", example = "A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11")
            @PathVariable UUID card_id
    ) {
        cardService.deleteCard(card_id);

        return ResponseEntity.noContent().build();
    }
}
