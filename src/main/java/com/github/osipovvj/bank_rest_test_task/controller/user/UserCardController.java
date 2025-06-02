package com.github.osipovvj.bank_rest_test_task.controller.user;

import com.github.osipovvj.bank_rest_test_task.dto.CardDto;
import com.github.osipovvj.bank_rest_test_task.dto.request.CardFilterRequest;
import com.github.osipovvj.bank_rest_test_task.dto.request.TransferRequest;
import com.github.osipovvj.bank_rest_test_task.dto.response.BalanceResponse;
import com.github.osipovvj.bank_rest_test_task.dto.response.GetCardsResponse;
import com.github.osipovvj.bank_rest_test_task.exception.dto.ProblemDetailResponse;
import com.github.osipovvj.bank_rest_test_task.service.UserCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user/cards")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
@Tag(
        name = "UserCardController",
        description = "Управление пользователем своими картами."
)
public class UserCardController {
    private final UserCardService userCardService;

    @Operation(summary = "Получение информации о карте по её ID.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение информации о карте.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CardDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Карта с таким ID не найдена.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailResponse.class)
                    )
            )
    })
    @GetMapping(value = "/{card_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE})
    public ResponseEntity<CardDto> getCard(
            @Parameter(description = "ID карты.", example = "A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11")
            @PathVariable UUID card_id,

            Authentication authentication
    ) {
        CardDto response = userCardService.getCard(authentication.getName(), card_id);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Получение списка пользовательских карт.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение списка.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GetCardsResponse.class)
                    )
            )
    })
    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetCardsResponse> getCards(
            @RequestBody CardFilterRequest request,
            Authentication authentication
    ) {
        GetCardsResponse response = userCardService.getCards(authentication.getName(), request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Запрос на получение баланса карты.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение баланса по карте.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BalanceResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Карта с таким ID не найдена.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailResponse.class)
                    )
            )
    })
    @GetMapping(value = "/{card_id}/balance", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE})
    public ResponseEntity<BalanceResponse> getBalance(
            @Parameter(description = "ID карты.", example = "A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11")
            @PathVariable UUID card_id,

            Authentication authentication
    ) {
        BalanceResponse response = userCardService.getBalance(authentication.getName(), card_id);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Запрос на блокировку карты.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Успешная отправка запроса на блокировку карты.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Карта с таким ID не найдена.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailResponse.class)
                    )
            )
    })
    @PostMapping(value = "/{card_id}/block", produces = MediaType.APPLICATION_PROBLEM_JSON_VALUE)
    public ResponseEntity<Void> blockCardResponse(
            @Parameter(description = "ID карты.", example = "A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11")
            @PathVariable UUID card_id,

            Authentication authentication
    ) {
        userCardService.blockCard(authentication.getName(), card_id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Запрос на перевод между своими картами.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешный перевод.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BalanceResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Карта с таким ID не найдена.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Недостаточно средств на карте.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailResponse.class)
                    )
            )
    })
    @PostMapping(value = "/transfer", consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE})
    public ResponseEntity<BalanceResponse> transfer(@RequestBody TransferRequest request, Authentication authentication) {
        BalanceResponse response = userCardService.transfer(authentication.getName(), request);

        return ResponseEntity.ok(response);
    }
}
