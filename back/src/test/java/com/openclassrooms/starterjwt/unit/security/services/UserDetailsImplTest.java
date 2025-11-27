package com.openclassrooms.starterjwt.unit.security.services;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsImplTest {

    @Test
    void builderShouldCreateObjectWithCorrectValues() {
        UserDetailsImpl user = UserDetailsImpl.builder()
                .id(1L)
                .username("john@example.com")
                .firstName("John")
                .lastName("Doe")
                .admin(true)
                .password("secret")
                .build();

        assertEquals(1L, user.getId());
        assertEquals("john@example.com", user.getUsername());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertTrue(user.getAdmin());
        assertEquals("secret", user.getPassword());
    }

    @Test
    void getAuthoritiesShouldReturnEmptyCollection() {
        UserDetailsImpl user = UserDetailsImpl.builder().build();

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    @Test
    void securityMethodsShouldReturnTrue() {
        UserDetailsImpl user = UserDetailsImpl.builder().build();

        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }

    // ----------------------------
    // equals()
    // ----------------------------

    @Test
    void equalsShouldReturnTrueForSameObject() {
        UserDetailsImpl user = UserDetailsImpl.builder().id(1L).build();

        assertEquals(user, user);
    }

    @Test
    void equalsShouldReturnTrueForSameId() {
        UserDetailsImpl u1 = UserDetailsImpl.builder().id(2L).build();
        UserDetailsImpl u2 = UserDetailsImpl.builder().id(2L).build();

        assertEquals(u1, u2);
    }

    @Test
    void equalsShouldReturnFalseForDifferentId() {
        UserDetailsImpl u1 = UserDetailsImpl.builder().id(3L).build();
        UserDetailsImpl u2 = UserDetailsImpl.builder().id(4L).build();

        assertNotEquals(u1, u2);
    }

    @Test
    void equalsShouldReturnFalseWhenComparingWithNull() {
        UserDetailsImpl user = UserDetailsImpl.builder().id(5L).build();

        assertNotEquals(user, null);
    }

    @Test
    void equalsShouldReturnFalseWhenDifferentClass() {
        UserDetailsImpl user = UserDetailsImpl.builder().id(5L).build();
        String other = "not a UserDetailsImpl";

        assertNotEquals(user, other);
    }
}
