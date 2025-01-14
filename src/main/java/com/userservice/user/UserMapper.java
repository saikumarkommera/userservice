package com.userservice.user;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toUser(UserDTO dto){
        return User.builder()
                .name(dto.name())
                .email(dto.email())
                .build();
    }

    public UserDTO toUserDTO(User user){
        return new UserDTO(user.getId(), user.getName(), user.getEmail());
    }
}
