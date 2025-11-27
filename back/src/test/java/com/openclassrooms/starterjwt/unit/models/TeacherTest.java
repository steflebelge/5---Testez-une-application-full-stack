package com.openclassrooms.starterjwt.unit.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TeacherTest {

    @Test
    void builderShouldCreateTeacherCorrectly() {
        LocalDateTime now = LocalDateTime.now();

        Teacher teacher = Teacher.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(1L, teacher.getId());
        assertEquals("John", teacher.getFirstName());
        assertEquals("Doe", teacher.getLastName());
        assertEquals(now, teacher.getCreatedAt());
        assertEquals(now, teacher.getUpdatedAt());
    }

    @Test
    void settersAndGettersShouldWork() {
        Teacher teacher = new Teacher();

        LocalDateTime created = LocalDateTime.now();
        LocalDateTime updated = LocalDateTime.now();

        teacher.setId(2L);
        teacher.setFirstName("Alice");
        teacher.setLastName("Smith");
        teacher.setCreatedAt(created);
        teacher.setUpdatedAt(updated);

        assertEquals(2L, teacher.getId());
        assertEquals("Alice", teacher.getFirstName());
        assertEquals("Smith", teacher.getLastName());
        assertEquals(created, teacher.getCreatedAt());
        assertEquals(updated, teacher.getUpdatedAt());
    }

    @Test
    void chainAccessorsShouldWork() {
        Teacher t = new Teacher()
                .setId(3L)
                .setFirstName("Chained")
                .setLastName("User");

        assertEquals(3L, t.getId());
        assertEquals("Chained", t.getFirstName());
        assertEquals("User", t.getLastName());
    }

    @Test
    void equalsShouldUseIdOnly() {
        Teacher t1 = Teacher.builder().id(10L).firstName("A").lastName("B").build();
        Teacher t2 = Teacher.builder().id(10L).firstName("X").lastName("Y").build();
        Teacher t3 = Teacher.builder().id(20L).firstName("A").lastName("B").build();

        assertEquals(t1, t2);
        assertNotEquals(t1, t3);
    }

    @Test
    void toStringShouldNotThrow() {
        Teacher t = Teacher.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .build();

        String result = t.toString();

        assertNotNull(result);
        assertTrue(result.contains("John"));
        assertTrue(result.contains("Doe"));
    }
}
