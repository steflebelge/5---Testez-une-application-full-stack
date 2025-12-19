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

        assertNotEquals(null, user);
    }

    @Test
    void equalsShouldReturnFalseWhenDifferentClass() {
        UserDetailsImpl user = UserDetailsImpl.builder().id(5L).build();
        String other = "not a UserDetailsImpl";

        assertNotEquals(other, user);
    }

    @Test
    void equalsShouldHandleNullIds() {
        UserDetailsImpl u1 = UserDetailsImpl.builder().id(null).build();
        UserDetailsImpl u2 = UserDetailsImpl.builder().id(10L).build();

        assertNotEquals(u1, u2);        // id null != id non null
        assertNotEquals(u2, u1);        // inverse
        assertEquals(u1, u1);           // même instance
    }

    @Test
    void getAdminShouldHandleTrueFalseAndNull() {
        UserDetailsImpl userTrue = UserDetailsImpl.builder().admin(true).build();
        UserDetailsImpl userFalse = UserDetailsImpl.builder().admin(false).build();
        UserDetailsImpl userNull = UserDetailsImpl.builder().admin(null).build();

        assertTrue(userTrue.getAdmin());
        assertFalse(userFalse.getAdmin());
        assertNull(userNull.getAdmin());
    }

    @Test
    void equals_shouldCoverAllBranches() {
        UserDetailsImpl uNullId1 = UserDetailsImpl.builder().id(null).build();
        UserDetailsImpl uNullId2 = UserDetailsImpl.builder().id(null).build();
        UserDetailsImpl uId1 = UserDetailsImpl.builder().id(1L).build();
        UserDetailsImpl uId2 = UserDetailsImpl.builder().id(2L).build();

        // Branche 1: même instance
        assertEquals(uId1, uId1);

        // Branche 2 et 3: null ou autre classe
        assertNotEquals(uId1, null);
        assertNotEquals(uId1, "string");

        // Branche 4: id null && id null
        assertEquals(uNullId1, uNullId2);

        // Branche 5: id null vs id non null
        assertNotEquals(uNullId1, uId1);
        assertNotEquals(uId1, uNullId1);

        // Branche 6: id non null égaux / différents
        UserDetailsImpl uId1Copy = UserDetailsImpl.builder().id(1L).build();
        assertEquals(uId1, uId1Copy);
        assertNotEquals(uId1, uId2);
    }
}
