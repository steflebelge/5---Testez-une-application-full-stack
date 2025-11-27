package com.openclassrooms.starterjwt.unit.mapper;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.unit.models.User;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper mapper = new UserMapperImpl();

    @Test
    void toDto_shouldMapUserToDto() {
        User user = new User();
        user.setId(1L);
        user.setEmail("john@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("pwd");
        user.setAdmin(true);

        UserDto dto = mapper.toDto(user);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("john@example.com", dto.getEmail());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertTrue(dto.isAdmin());
    }

    @Test
    void toEntity_shouldMapDtoToUser() {
        UserDto dto = new UserDto();
        dto.setId(2L);
        dto.setEmail("alice@example.com");
        dto.setFirstName("Alice");
        dto.setLastName("Smith");
        dto.setPassword("pwd");
        dto.setAdmin(false);

        User user = mapper.toEntity(dto);

        assertNotNull(user);
        assertEquals(2L, user.getId());
        assertEquals("alice@example.com", user.getEmail());
        assertEquals("Alice", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertFalse(user.isAdmin());
    }
}
