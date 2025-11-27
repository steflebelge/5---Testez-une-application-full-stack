package com.openclassrooms.starterjwt.unit.payload.request;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        LoginRequest req = new LoginRequest();

        req.setEmail("test@example.com");
        req.setPassword("secret");

        assertEquals("test@example.com", req.getEmail());
        assertEquals("secret", req.getPassword());
    }

    @Test
    void getter_shouldReturnInitialNullValues() {
        LoginRequest req = new LoginRequest();

        assertNull(req.getEmail());
        assertNull(req.getPassword());
    }
}
