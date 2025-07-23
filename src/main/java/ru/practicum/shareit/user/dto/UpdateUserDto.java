package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UpdateUserDto {

    private Long userId;
    private String name;
    private String email;

    public boolean hasUserId() {
        return userId != null && userId > 0;
    }

    public boolean hasName() {
        return !(name == null || name.strip().isBlank());
    }

    public boolean hasEmail() {
        return !(email == null || email.strip().isBlank());
    }
}
