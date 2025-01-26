package com.userservice.user;

public record UserDTO(
        Long id,
        String name,
        String email
) {
}
