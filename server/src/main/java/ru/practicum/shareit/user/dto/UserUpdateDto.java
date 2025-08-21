package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {

    private Long userId;
    private String email;
    private String name;

    public boolean hasName() {
        return !(name == null || name.trim().isBlank());
    }

    public boolean hasEmail() {
        return !(email == null || email.trim().isBlank());
    }
}
