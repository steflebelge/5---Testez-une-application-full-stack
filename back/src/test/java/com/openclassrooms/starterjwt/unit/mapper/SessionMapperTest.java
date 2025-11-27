package com.openclassrooms.starterjwt.unit.mapper;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.unit.models.Session;
import com.openclassrooms.starterjwt.unit.models.Teacher;
import com.openclassrooms.starterjwt.unit.models.User;
import com.openclassrooms.starterjwt.unit.services.TeacherService;
import com.openclassrooms.starterjwt.unit.services.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SessionMapperTest {

    @Mock
    private TeacherService teacherService;

    @Mock
    private UserService userService;

    private SessionMapper mapper;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // MapStruct-generated implementation
        mapper = new SessionMapperImpl();

        inject(mapper, "teacherService", teacherService);
        inject(mapper, "userService", userService);
    }

    private void inject(Object target, String field, Object value) throws Exception {
        Field f = target.getClass().getSuperclass().getDeclaredField(field);
        f.setAccessible(true);
        f.set(target, value);
    }

    // --------------------------------------------------------------------
    // toEntity()
    // --------------------------------------------------------------------

    @Test
    void toEntity_shouldMapAllFieldsCorrectly() {
        SessionDto dto = new SessionDto();
        dto.setId(1L);
        dto.setDescription("desc");
        dto.setTeacher_id(10L);
        dto.setUsers(Arrays.asList(100L, 200L));

        Teacher teacher = new Teacher();
        teacher.setId(10L);

        User u1 = new User();
        u1.setId(100L);

        User u2 = new User();
        u2.setId(200L);

        when(teacherService.findById(10L)).thenReturn(teacher);
        when(userService.findById(100L)).thenReturn(u1);
        when(userService.findById(200L)).thenReturn(u2);

        Session entity = mapper.toEntity(dto);

        assertEquals("desc", entity.getDescription());
        assertEquals(teacher, entity.getTeacher());
        assertEquals(2, entity.getUsers().size());
        assertTrue(entity.getUsers().contains(u1));
        assertTrue(entity.getUsers().contains(u2));
    }

    @Test
    void toEntity_shouldHandleNullTeacher() {
        SessionDto dto = new SessionDto();
        dto.setTeacher_id(null);

        Session entity = mapper.toEntity(dto);

        assertNull(entity.getTeacher());
    }

    @Test
    void toEntity_shouldIgnoreMissingUsers() {
        SessionDto dto = new SessionDto();
        dto.setUsers(Arrays.asList(1L, 2L, 3L));

        when(userService.findById(anyLong())).thenReturn(null);

        Session entity = mapper.toEntity(dto);

        assertEquals(3, entity.getUsers().size());
        assertNull(entity.getUsers().get(0)); // lisible et attendu
    }

    @Test
    void toEntity_shouldHandleNullUserList() {
        SessionDto dto = new SessionDto();
        dto.setUsers(null);

        Session entity = mapper.toEntity(dto);

        assertNotNull(entity.getUsers());
        assertEquals(0, entity.getUsers().size());
    }

    // --------------------------------------------------------------------
    // toDto()
    // --------------------------------------------------------------------

    @Test
    void toDto_shouldMapEntityToDto() {
        Session s = new Session();
        s.setId(1L);
        s.setDescription("abc");

        Teacher teacher = new Teacher();
        teacher.setId(5L);
        s.setTeacher(teacher);

        User u1 = new User();
        u1.setId(10L);
        User u2 = new User();
        u2.setId(20L);

        s.setUsers(Arrays.asList(u1, u2));

        SessionDto dto = mapper.toDto(s);

        assertEquals("abc", dto.getDescription());
        assertEquals(5L, dto.getTeacher_id());
        assertEquals(Arrays.asList(10L, 20L), dto.getUsers());
    }

    @Test
    void toDto_shouldHandleNullUsers() {
        Session s = new Session();
        s.setUsers(null);

        SessionDto dto = mapper.toDto(s);

        assertNotNull(dto.getUsers());
        assertEquals(0, dto.getUsers().size());
    }

    @Test
    void toDto_shouldHandleNullTeacher() {
        Session s = new Session();
        s.setTeacher(null);

        SessionDto dto = mapper.toDto(s);

        assertNull(dto.getTeacher_id());
    }
}
