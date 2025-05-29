package com.github.osipovvj.bank_rest_test_task.controller.admin;

import com.github.osipovvj.bank_rest_test_task.dto.UserDto;
import com.github.osipovvj.bank_rest_test_task.dto.request.UpdateUserRequest;
import com.github.osipovvj.bank_rest_test_task.dto.response.GetAllUsersResponse;
import com.github.osipovvj.bank_rest_test_task.exception.dto.ProblemDetailResponse;
import com.github.osipovvj.bank_rest_test_task.service.UserService;
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
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(
        name = "AdminUserController",
        description = "Управление пользователями (просмотр, изменение, удаление). Только для админов."
)
public class AdminUserController {
    private final UserService userService;

    @Operation(summary = "Получение списка всех пользователей с постраничной выдачей и сортировкой от первого зарегестрированного.")
    @ApiResponse(
            responseCode = "200",
            description = "Успешной получение списка пользователей.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GetAllUsersResponse.class)
            )
    )
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetAllUsersResponse> getUsers(
            @Parameter(description = "Номер страницы.", example = "0")
            @RequestParam(required = false, defaultValue = "0")
            int offset,

            @Parameter(description = "Количество элементов на странице.", example = "100")
            @RequestParam(required = false, defaultValue = "100")
            int limit
    ) {
        GetAllUsersResponse response = userService.getAllUsers(offset, limit);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Получение информации о пользователе по ID.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение данных пользователя.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь с таким ID не найден.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailResponse.class)
                    )
            )
    })
    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE})
    public ResponseEntity<UserDto> getUser(
            @Parameter(description = "ID пользователя", example = "A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11")
            @PathVariable UUID id
    ) {
        UserDto user = userService.getUserById(id);

        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Изменение данных пользователя.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Данные пользователя успешно изменены.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверный формат данных.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь с таким ID не найден.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Пользователь с указанными данными уже зарегестрирован.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailResponse.class)
                    )
            )
    })
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE})
    public ResponseEntity<UserDto> updateUser(
            @Parameter(description = "ID пользователя", example = "A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11")
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        UserDto user = userService.updateUser(id, request);

        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Удаление пользователя.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Успешное удаление пользоваетля.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь с таким ID не найден.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailResponse.class)
                    )
            )
    })
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_PROBLEM_JSON_VALUE)
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID пользвотеля.", example = "A0EEBC99-9C0B-4EF8-BB6D-6BB9BD380A11")
            @PathVariable UUID id
    ) {
        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }
}
