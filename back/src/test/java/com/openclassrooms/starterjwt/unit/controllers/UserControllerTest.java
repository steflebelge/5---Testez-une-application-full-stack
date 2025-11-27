package com.openclassrooms.starterjwt.unit.controllers;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.unit.mapper.UserMapper;
import com.openclassrooms.starterjwt.unit.models.User;
import com.openclassrooms.starterjwt.unit.security.services.UserDetailsImpl;
import com.openclassrooms.starterjwt.unit.services.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    private UserController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new UserController(userService, userMapper);
        SecurityContextHolder.clearContext();
    }

    // --------------------------------------------------------------------
    // GET /api/user/{id}
    // --------------------------------------------------------------------

    @Test
    void findById_shouldReturnOk_whenUserFound() {
        User user = new User();
        user.setId(1L);

        UserDto dto = new UserDto();

        when(userService.findById(1L)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(dto);

        ResponseEntity<?> response = controller.findById("1");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    void findById_shouldReturnNotFound_whenUserNotFound() {
        when(userService.findById(1L)).thenReturn(null);

        ResponseEntity<?> response = controller.findById("1");

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void findById_shouldReturnBadRequest_whenIdInvalid() {
        ResponseEntity<?> response = controller.findById("abc");

        assertEquals(400, response.getStatusCodeValue());
        verify(userService, never()).findById(anyLong());
    }

    // --------------------------------------------------------------------
    // DELETE /api/user/{id}
    // --------------------------------------------------------------------

    @Test
    void delete_shouldReturnBadRequest_whenIdInvalid() {
        ResponseEntity<?> response = controller.save("xyz");

        assertEquals(400, response.getStatusCodeValue());
        verify(userService, never()).findById(anyLong());
    }

    @Test
    void delete_shouldReturnNotFound_whenUserNotFound() {
        when(userService.findById(1L)).thenReturn(null);

        ResponseEntity<?> response = controller.save("1");

        assertEquals(404, response.getStatusCodeValue());
        verify(userService, never()).delete(anyLong());
    }

    @Test
    void delete_shouldReturnUnauthorized_whenConnectedUserDiffers() {
        User user = new User();
        user.setId(1L);
        user.setEmail("realuser@example.com");

        when(userService.findById(1L)).thenReturn(user);

        UserDetailsImpl principal = UserDetailsImpl.builder()
                .id(999L)
                .username("another@example.com")
                .firstName("X")
                .lastName("Y")
                .password("pwd")
                .admin(false)
                .build();

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);

        ResponseEntity<?> response = controller.save("1");

        assertEquals(401, response.getStatusCodeValue());
        verify(userService, never()).delete(anyLong());
    }

    @Test
    void delete_shouldReturnOk_whenConnectedUserMatches() {
        User user = new User();
        user.setId(1L);
        user.setEmail("match@example.com");

        when(userService.findById(1L)).thenReturn(user);

        UserDetailsImpl principal = UserDetailsImpl.builder()
                .id(1L)
                .username("match@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("pwd")
                .admin(false)
                .build();

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        ResponseEntity<?> response = controller.save("1");

        assertEquals(200, response.getStatusCodeValue());
        verify(userService).delete(1L);
    }
}
