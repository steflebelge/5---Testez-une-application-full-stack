package com.openclassrooms.starterjwt.unit.payload.request;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SignupRequestTest {

    @Test
    void gettersShouldReturnNullByDefault() {
        SignupRequest req = new SignupRequest();

        assertNull(req.getEmail());
        assertNull(req.getFirstName());
        assertNull(req.getLastName());
        assertNull(req.getPassword());
    }

    @Test
    void settersShouldAssignValuesCorrectly() {
        SignupRequest req = new SignupRequest();

        req.setEmail("test@example.com");
        req.setFirstName("John");
        req.setLastName("Doe");
        req.setPassword("secretPass");

        assertEquals("test@example.com", req.getEmail());
        assertEquals("John", req.getFirstName());
        assertEquals("Doe", req.getLastName());
        assertEquals("secretPass", req.getPassword());
    }

    @Test
    void lombokGeneratedMethodsShouldBeCovered() {
        SignupRequest r1 = new SignupRequest();
        r1.setEmail("a@b.com");
        r1.setFirstName("John");
        r1.setLastName("Doe");
        r1.setPassword("pass123");

        SignupRequest r2 = new SignupRequest();
        r2.setEmail("a@b.com");
        r2.setFirstName("John");
        r2.setLastName("Doe");
        r2.setPassword("pass123");

        // equals
        assertEquals(r1, r2);
        assertNotEquals(r1, new SignupRequest());

        // hashCode
        assertEquals(r1.hashCode(), r2.hashCode());

        // toString
        assertNotNull(r1.toString());
        assertTrue(r1.toString().contains("a@b.com"));

        // canEqual
        assertTrue(r1.canEqual(r2));
        assertFalse(r1.canEqual("not a SignupRequest"));
    }
}
