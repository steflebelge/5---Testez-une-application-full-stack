package com.openclassrooms.starterjwt.unit.services;

import com.openclassrooms.starterjwt.unit.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void delete_shouldCallRepositoryDeleteById() {
        Long userId = 1L;

        userService.delete(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void findById_shouldReturnUserWhenFound() {
        Long userId = 2L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.findById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void findById_shouldReturnNullWhenNotFound() {
        Long userId = 3L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        User result = userService.findById(userId);

        assertNull(result);
        verify(userRepository, times(1)).findById(userId);
    }
}
