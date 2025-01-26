package com.userservice.service;

import com.userservice.exception.UserException;
import com.userservice.repository.UserRepository;
import com.userservice.user.User;
import com.userservice.user.UserDTO;
import com.userservice.user.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private UserMapper mapper;

    @Mock
    private RedisService redis;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void testCreateNewUser() {
        // Arrange
        UserDTO dto = new UserDTO(1l, "John Doe", "john@example.com");
        User user = new User(1l, "John Doe", "john@example.com");
        when(mapper.toUser(dto)).thenReturn(user);
        when(repository.save(user)).thenReturn(user);

        // Act
        User result = userService.createNewUser(dto);

        // Assert
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        verify(mapper, times(1)).toUser(dto);
        verify(repository, times(1)).save(user);
    }

    @Test
    void testGetUser_FromCache() {
        // Arrange
        long userId = 1;
        User cachedUser = new User(userId, "John Doe", "john@example.com");
        when(redis.get(String.valueOf(userId))).thenReturn(cachedUser);

        // Act
        User result = userService.getUser((int) userId);

        // Assert
        assertNotNull(result);
        assertEquals(cachedUser.getId(), result.getId());
        assertEquals(cachedUser.getName(), result.getName());
        assertEquals(cachedUser.getEmail(), result.getEmail());
        verify(redis, times(1)).get(String.valueOf(userId));
        verify(repository, never()).findById((int) userId);
    }

    @Test
    void testGetUser_FromDatabase() {
        // Arrange
        long userId = 1;
        User user = new User(userId, "John Doe", "john@example.com");
        when(redis.get(String.valueOf(userId))).thenReturn(null);
        when(repository.findById((int) userId)).thenReturn(Optional.of(user));

        // Act
        User result = userService.getUser((int) userId);

        // Assert
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        verify(redis, times(1)).get(String.valueOf(userId));
        verify(repository, times(1)).findById((int) userId);
        verify(redis, times(1)).set(String.valueOf(userId), user, 3000L);
    }

    @Test
    void testGetUser_UserNotFound() {
        // Arrange
        int userId = 1;
        when(redis.get(String.valueOf(userId))).thenReturn(null);
        when(repository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        UserException exception = assertThrows(UserException.class, () -> userService.getUser(userId));
        assertEquals("No User Found with id :" + userId, exception.getMessage());
        verify(redis, times(1)).get(String.valueOf(userId));
        verify(repository, times(1)).findById(userId);
        verify(redis, never()).set(anyString(), any(User.class), anyLong());
    }

    @Test
    void testDeleteUser() {
        // Arrange
        long userId = 1;
        User user = new User(userId, "John Doe", "john@example.com");
        when(repository.findById((int) userId)).thenReturn(Optional.of(user));
        doNothing().when(repository).deleteById((int) userId);
        doNothing().when(redis).delete(String.valueOf(userId));

        // Act
        userService.deleteUser((int) userId);

        // Assert
        verify(repository, times(1)).findById((int) userId);
        verify(repository, times(1)).deleteById((int) userId);
        verify(redis, times(1)).delete(String.valueOf(userId));
    }

    @Test
    void testDeleteUser_UserNotFound() {
        // Arrange
        int userId = 1;
        when(repository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        UserException exception = assertThrows(UserException.class, () -> userService.deleteUser(userId));
        assertEquals("No User Found with id :" + userId, exception.getMessage());
        verify(repository, times(1)).findById(userId);
        verify(repository, never()).deleteById(userId);
        verify(redis, never()).delete(anyString());
    }

    @Test
    void testUpdateUser() {
        // Arrange
        long userId = 1;
        UserDTO dto = new UserDTO(userId, "John Doe Updated", "john.updated@example.com");
        User existingUser = new User(userId, "John Doe", "john@example.com");
        User updatedUser = new User(userId, "John Doe Updated", "john.updated@example.com");
        when(repository.findById((int) userId)).thenReturn(Optional.of(existingUser));
        when(repository.save(existingUser)).thenReturn(updatedUser);

        // Act
        User result = userService.updateUser((int) userId, dto);

        // Assert
        assertNotNull(result);
        assertEquals(updatedUser.getId(), result.getId());
        assertEquals(updatedUser.getName(), result.getName());
        assertEquals(updatedUser.getEmail(), result.getEmail());
        verify(repository, times(1)).findById((int) userId);
        verify(repository, times(1)).save(existingUser);
        verify(redis, times(1)).set(String.valueOf(userId), updatedUser, 3000L);
    }

    @Test
    void testUpdateUser_UserNotFound() {
        // Arrange
        long userId = 1;
        UserDTO dto = new UserDTO(userId, "John Doe Updated", "john.updated@example.com");
        when(repository.findById((int) userId)).thenReturn(Optional.empty());

        // Act & Assert
        UserException exception = assertThrows(UserException.class, () -> userService.updateUser((int) userId, dto));
        assertEquals("No User Found with id :" + userId, exception.getMessage());
        verify(repository, times(1)).findById((int) userId);
        verify(repository, never()).save(any(User.class));
        verify(redis, never()).set(anyString(), any(User.class), anyLong());
    }
}