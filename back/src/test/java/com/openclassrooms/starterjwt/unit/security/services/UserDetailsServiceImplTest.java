package com.openclassrooms.starterjwt.unit.security.services;

import com.openclassrooms.starterjwt.unit.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserDetailsServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetailsWhenUserExists() {
        User user = new User();
        user.setId(1L);
        user.setEmail("john@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("secret");

        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(user));

        UserDetails result = service.loadUserByUsername("john@example.com");

        assertNotNull(result);
        assertEquals("john@example.com", result.getUsername());
        assertEquals("secret", result.getPassword());

        UserDetailsImpl details = (UserDetailsImpl) result;

        assertEquals(1L, details.getId());
        assertEquals("John", details.getFirstName());
        assertEquals("Doe", details.getLastName());

        verify(userRepository, times(1)).findByEmail("john@example.com");
    }

    @Test
    void loadUserByUsername_shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("unknown@example.com")
        );

        assertEquals("User Not Found with email: unknown@example.com", ex.getMessage());
        verify(userRepository, times(1)).findByEmail("unknown@example.com");
    }
}
