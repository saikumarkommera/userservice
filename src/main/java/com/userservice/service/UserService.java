package com.userservice.service;

import com.userservice.repository.UserRepository;
import com.userservice.user.User;
import com.userservice.user.UserDTO;
import com.userservice.user.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository repository;
    private final UserMapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @CachePut(value = "user" , key = "#result.id")
    public User createNewUser(UserDTO dto) {
        User user = this.repository.save(mapper.toUser(dto));
        logger.info("User got created with Id : "+user.getId());
        return user;
    }

    @Cacheable(value = "user" , key = "#id")
    public User getUser(int id) throws Exception {
        User user = this.repository.findById(id).orElseThrow(() -> new RuntimeException("No User Found..!"));
        logger.info("Got user from DB : "+user);
        return user;
    }

    @CacheEvict(value = "user",key = "#id")
    public void deleteUser(int id) {
        this.repository.deleteById(id);
        logger.info("User deleted with Id : "+user.getId());
    }

    @CachePut(value = "user" , key = "#result.id")
    public User updateUser(int id,UserDTO dto) {
        User user = this.repository.findById(id).orElseThrow(()-> new RuntimeException("User not found..!"));
        user.setEmail(dto.email());
        user.setName(dto.name());
        logger.info("updating user with id : "+user.getId()+" in db");
        return this.repository.save(user);
    }
}
