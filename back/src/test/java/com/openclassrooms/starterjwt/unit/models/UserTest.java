package com.openclassrooms.starterjwt.unit.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void builderShouldCreateUserCorrectly() {
        LocalDateTime now = LocalDateTime.now();

        User user = User.builder()
                .id(1L)
                .email("john@example.com")
                .lastName("Doe")
                .firstName("John")
                .password("pwd123")
                .admin(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(1L, user.getId());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("Doe", user.getLastName());
        assertEquals("John", user.getFirstName());
        assertEquals("pwd123", user.getPassword());
        assertTrue(user.isAdmin());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }

    @Test
    void requiredArgsConstructorShouldWork() {
        User user = new User(
                "jane@example.com",
                "Smith",
                "Jane",
                "pwd456",
                false
        );

        assertNull(user.getId()); // id non fourni
        assertEquals("jane@example.com", user.getEmail());
        assertEquals("Smith", user.getLastName());
        assertEquals("Jane", user.getFirstName());
        assertEquals("pwd456", user.getPassword());
        assertFalse(user.isAdmin());
    }

    @Test
    void allArgsConstructorShouldWork() {
        LocalDateTime now = LocalDateTime.now();

        User user = new User(
                5L,
                "a@b.com",
                "Last",
                "First",
                "pwd",
                true,
                now,
                now
        );

        assertEquals(5L, user.getId());
        assertEquals("a@b.com", user.getEmail());
        assertEquals("Last", user.getLastName());
        assertEquals("First", user.getFirstName());
        assertEquals("pwd", user.getPassword());
        assertTrue(user.isAdmin());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }

    @Test
    void settersAndGettersShouldWork() {
        User user = new User();

        LocalDateTime created = LocalDateTime.now();
        LocalDateTime updated = LocalDateTime.now();

        user.setId(2L);
        user.setEmail("test@example.com");
        user.setLastName("Lastname");
        user.setFirstName("Firstname");
        user.setPassword("123");
        user.setAdmin(true);
        user.setCreatedAt(created);
        user.setUpdatedAt(updated);

        assertEquals(2L, user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Lastname", user.getLastName());
        assertEquals("Firstname", user.getFirstName());
        assertEquals("123", user.getPassword());
        assertTrue(user.isAdmin());
        assertEquals(created, user.getCreatedAt());
        assertEquals(updated, user.getUpdatedAt());
    }

    @Test
    void chainAccessorsShouldWork() {
        User user = new User()
                .setId(7L)
                .setEmail("x@y.com")
                .setLastName("LN")
                .setFirstName("FN")
                .setPassword("pwd")
                .setAdmin(false);

        assertEquals(7L, user.getId());
        assertEquals("x@y.com", user.getEmail());
        assertEquals("LN", user.getLastName());
        assertEquals("FN", user.getFirstName());
        assertEquals("pwd", user.getPassword());
        assertFalse(user.isAdmin());
    }

    @Test
    void equalsShouldUseIdOnly() {
        User u1 = User.builder().id(10L).email("a").lastName("b").firstName("c").password("x").admin(true).build();
        User u2 = User.builder().id(10L).email("z").lastName("y").firstName("x").password("w").admin(false).build();
        User u3 = User.builder().id(20L).email("a").lastName("b").firstName("c").password("x").admin(true).build();

        assertEquals(u1, u2);   // même id → égaux
        assertNotEquals(u1, u3); // id différent → pas égaux
    }

    @Test
    void toStringShouldNotThrow() {
        User user = User.builder()
                .id(1L)
                .email("john@example.com")
                .lastName("Doe")
                .firstName("John")
                .password("pwd")
                .build();

        String result = user.toString();

        assertNotNull(result);
        assertTrue(result.contains("john@example.com"));
        assertTrue(result.contains("Doe"));
    }
}
