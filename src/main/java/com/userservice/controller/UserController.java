package com.userservice.controller;

import com.userservice.service.UserService;
import com.userservice.user.User;
import com.userservice.user.UserDTO;
import com.userservice.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final UserMapper mapper;

    @PostMapping("/createUser")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO dto){
        User user = this.userService.createNewUser(dto);
        return ResponseEntity.ok(mapper.toUserDTO(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") int id) throws Exception {
        User user = this.userService.getUser(id);
        return ResponseEntity.ok(mapper.toUserDTO(user));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserDTO> updateUserById(@PathVariable("id") int id ,@RequestBody UserDTO dto) {
        User user = this.userService.updateUser(id,dto);
        return ResponseEntity.ok(mapper.toUserDTO(user));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable("id") int id) {
        this.userService.deleteUser(id);
        return ResponseEntity.ok("User Deleted successfully..!");
    }
}
