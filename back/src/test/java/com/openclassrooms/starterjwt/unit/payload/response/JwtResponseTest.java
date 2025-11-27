package com.openclassrooms.starterjwt.unit.payload.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JwtResponseTest {

    @Test
    void constructor_shouldInitializeFields() {
        JwtResponse response = new JwtResponse(
                "token123",
                10L,
                "user@example.com",
                "John",
                "Doe",
                true
        );

        assertEquals("token123", response.getToken());
        assertEquals(10L, response.getId());
        assertEquals("user@example.com", response.getUsername());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertTrue(response.getAdmin());
        assertEquals("Bearer", response.getType());
    }

    @Test
    void setters_shouldUpdateValues() {
        JwtResponse response = new JwtResponse(
                "t", 1L, "u", "f", "l", false
        );

        response.setToken("newToken");
        response.setId(99L);
        response.setUsername("newUser");
        response.setFirstName("Alice");
        response.setLastName("Smith");
        response.setAdmin(true);
        response.setType("Custom");

        assertEquals("newToken", response.getToken());
        assertEquals(99L, response.getId());
        assertEquals("newUser", response.getUsername());
        assertEquals("Alice", response.getFirstName());
        assertEquals("Smith", response.getLastName());
        assertTrue(response.getAdmin());
        assertEquals("Custom", response.getType());
    }

    @Test
    void getters_shouldReturnCurrentValues() {
        JwtResponse response = new JwtResponse(
                "abc", 2L, "me", "A", "B", false
        );

        assertEquals("abc", response.getToken());
        assertEquals(2L, response.getId());
        assertEquals("me", response.getUsername());
        assertEquals("A", response.getFirstName());
        assertEquals("B", response.getLastName());
        assertFalse(response.getAdmin());
        assertEquals("Bearer", response.getType());
    }
}
