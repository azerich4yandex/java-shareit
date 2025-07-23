package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NewUserDto {

    private String email;
    private String name;
}
