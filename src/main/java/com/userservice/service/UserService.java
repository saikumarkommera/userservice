package com.userservice.service;

import com.userservice.repository.UserRepository;
import com.userservice.user.User;
import com.userservice.user.UserDTO;
import com.userservice.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    public int createNewUser(UserDTO user) {
        return this.repository.save(mapper.toUser(user)).getId();
    }

    public UserDTO getUser(int id) throws Exception {
        return this.repository.findById(id).map(mapper::toUserDTO).orElseThrow(() -> new RuntimeException("No User Found..!"));
    }

    public void deleteUser(int id) {
        this.repository.deleteById(id);
    }

    public void updateUser(int id,UserDTO dto) {
        User user = this.repository.findById(id).orElseThrow(()-> new RuntimeException("User not found..!"));
        user.setEmail(dto.email());
        user.setName(dto.name());
        this.repository.save(user);
    }
}
