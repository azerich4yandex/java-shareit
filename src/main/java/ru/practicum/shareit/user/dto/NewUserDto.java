package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NewUserDto {

    @NotNull(message = "Почтовый адрес пользователя должен быть указан")
    @Email(message = "Почтовый адрес пользователя должен быт указан корректно")
    private String email;

    @NotBlank(message = "Имя пользователя должно быть указано")
    private String name;
}
