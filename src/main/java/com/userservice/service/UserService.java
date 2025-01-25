package com.userservice.service;

import com.userservice.repository.UserRepository;
import com.userservice.user.User;
import com.userservice.user.UserDTO;
import com.userservice.user.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository repository;
    private final UserMapper mapper;
    private final RedisService redis;

    public User createNewUser(UserDTO dto) {
        User user = this.repository.save(mapper.toUser(dto));
        log.info("User got created with Id : "+user.getId());
        return user;
    }

    public User getUser(int id) throws Exception {
        Object userObj = this.redis.get(String.valueOf(id));
        if(userObj!=null){
            log.info("Gor user from cache");
            return (User)userObj;
        }
        User user = this.repository.findById(id).orElseThrow(() -> new RuntimeException("No User Found..!"));
        log.info("Got user from DB : "+user);
        this.redis.set(String.valueOf(id),user,3000l);
        return user;
    }

    public void deleteUser(int id) {
        User user = this.repository.findById(id).orElseThrow(()-> new RuntimeException("User not found..!"));
        this.repository.deleteById(id);
        this.redis.delete(String.valueOf(user.getId()));
        log.info("User deleted with Id : "+id);
    }

    public User updateUser(int id,UserDTO dto) {
        User user = this.repository.findById(id).orElseThrow(()-> new RuntimeException("User not found..!"));
        user.setEmail(dto.email());
        user.setName(dto.name());
        log.info("updating user with id : "+user.getId()+" in db");
        User updatedUser = this.repository.save(user);
        this.redis.set(String.valueOf(updatedUser.getId()),updatedUser,3000l);
        return updatedUser;
    }
}
