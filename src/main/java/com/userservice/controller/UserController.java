package com.userservice.controller;

import com.userservice.service.UserService;
import com.userservice.user.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/createUser")
    public ResponseEntity<Integer> createUser(@RequestBody UserDTO user){
        return ResponseEntity.ok(this.userService.createNewUser(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") int id) throws Exception {
        return ResponseEntity.ok(this.userService.getUser(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateUserById(@PathVariable("id") int id ,@RequestBody UserDTO user) {
        this.userService.updateUser(id,user);
        return ResponseEntity.ok("User updated successfully..!");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable("id") int id) {
        this.userService.deleteUser(id);
        return ResponseEntity.ok("User Deleted successfully..!");
    }
}
