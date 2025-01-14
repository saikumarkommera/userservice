package com.userservice.user;

import org.antlr.v4.runtime.misc.NotNull;

public record UserDTO(
        int id,
        String name,
        String email
) {
}
