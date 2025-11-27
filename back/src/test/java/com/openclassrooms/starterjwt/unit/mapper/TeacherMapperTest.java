package com.openclassrooms.starterjwt.unit.mapper;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.unit.models.Teacher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TeacherMapperTest {

    private final TeacherMapper mapper = new TeacherMapperImpl();

    @Test
    void toDto_shouldMapTeacherToDto() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");

        TeacherDto dto = mapper.toDto(teacher);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
    }

    @Test
    void toEntity_shouldMapDtoToTeacher() {
        TeacherDto dto = new TeacherDto();
        dto.setId(2L);
        dto.setFirstName("Alice");
        dto.setLastName("Smith");

        Teacher teacher = mapper.toEntity(dto);

        assertNotNull(teacher);
        assertEquals(2L, teacher.getId());
        assertEquals("Alice", teacher.getFirstName());
        assertEquals("Smith", teacher.getLastName());
    }
}
