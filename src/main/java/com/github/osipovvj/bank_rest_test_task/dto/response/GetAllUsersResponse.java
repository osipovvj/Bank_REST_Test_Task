package com.github.osipovvj.bank_rest_test_task.dto.response;

import com.github.osipovvj.bank_rest_test_task.dto.UserDto;
import com.github.osipovvj.bank_rest_test_task.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(name = "GetAllUsersResponse", description = "Ответ сервера со списком всех ползователей.")
public record GetAllUsersResponse(
        @Schema(description = "Общее количество пользователей.", example = "999")
        Long total,

        @Schema(description = "Количество страниц.", example = "10")
        Integer countOfPage,

        @Schema(description = "Номер страницы.", example = "0")
        Integer page,

        @Schema(description = "Количество пользователей на странице.", example = "10")
        Integer countOfElements,

        @Schema(description = "Список пользователей.")
        List<UserDto> users
) {
    public static GetAllUsersResponse toResponse(final Page<User> users) {
            return new GetAllUsersResponse(
                    users.getTotalElements(),
                    users.getTotalPages(),
                    users.getNumber(),
                    users.getNumberOfElements(),
                    users.get()
                            .map(UserDto::toDto)
                            .toList()
            );
    }
}
