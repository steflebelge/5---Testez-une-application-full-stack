package com.openclassrooms.starterjwt.unit.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SessionTest {

    @Test
    void builderShouldCreateSessionCorrectly() {
        Teacher teacher = new Teacher();
        teacher.setId(5L);

        User u1 = new User();
        u1.setId(10L);

        User u2 = new User();
        u2.setId(20L);

        Date date = new Date();

        Session session = Session.builder()
                .id(1L)
                .name("Yoga Class")
                .date(date)
                .description("A test description")
                .teacher(teacher)
                .users(Arrays.asList(u1, u2))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        assertEquals(1L, session.getId());
        assertEquals("Yoga Class", session.getName());
        assertEquals(date, session.getDate());
        assertEquals("A test description", session.getDescription());
        assertEquals(teacher, session.getTeacher());
        assertEquals(2, session.getUsers().size());
    }

    @Test
    void settersAndGettersShouldWork() {
        Session session = new Session();

        Teacher teacher = new Teacher();
        teacher.setId(99L);

        User user = new User();
        user.setId(100L);

        Date date = new Date();

        session.setId(2L);
        session.setName("Test");
        session.setDate(date);
        session.setDescription("Desc");
        session.setTeacher(teacher);
        session.setUsers(Collections.singletonList(user));
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());

        assertEquals(2L, session.getId());
        assertEquals("Test", session.getName());
        assertEquals(date, session.getDate());
        assertEquals("Desc", session.getDescription());
        assertEquals(teacher, session.getTeacher());
        assertEquals(1, session.getUsers().size());
    }

    @Test
    void chainAccessorsShouldWork() {
        Session session = new Session()
                .setId(3L)
                .setName("Chain Test")
                .setDescription("Chained")
                .setUsers(new ArrayList<>());

        assertEquals(3L, session.getId());
        assertEquals("Chain Test", session.getName());
        assertEquals("Chained", session.getDescription());
        assertNotNull(session.getUsers());
    }

    @Test
    void equalsShouldUseIdOnly() {
        Session s1 = Session.builder().id(10L).name("A").build();
        Session s2 = Session.builder().id(10L).name("B").build();
        Session s3 = Session.builder().id(20L).name("A").build();

        assertEquals(s1, s2);
        assertNotEquals(s1, s3);
    }

    @Test
    void toStringShouldNotThrow() {
        Session s = Session.builder()
                .id(1L)
                .name("Hello")
                .description("Test")
                .build();

        String sStr = s.toString();
        assertNotNull(sStr);
        assertTrue(sStr.contains("Hello"));
    }
}
