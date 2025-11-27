package com.openclassrooms.starterjwt.unit.services;

import com.openclassrooms.starterjwt.unit.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TeacherServiceTest {

    private TeacherRepository teacherRepository;
    private TeacherService teacherService;

    @BeforeEach
    void setUp() {
        teacherRepository = Mockito.mock(TeacherRepository.class);
        teacherService = new TeacherService(teacherRepository);
    }

    @Test
    void findAll_shouldReturnTeacherList() {
        Teacher t1 = new Teacher();
        t1.setId(1L);

        Teacher t2 = new Teacher();
        t2.setId(2L);

        List<Teacher> mockList = Arrays.asList(t1, t2);

        when(teacherRepository.findAll()).thenReturn(mockList);

        List<Teacher> result = teacherService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyListWhenNoTeachers() {
        when(teacherRepository.findAll()).thenReturn(Collections.emptyList());

        List<Teacher> result = teacherService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    void findById_shouldReturnTeacherWhenFound() {
        Long id = 10L;
        Teacher teacher = new Teacher();
        teacher.setId(id);

        when(teacherRepository.findById(id)).thenReturn(Optional.of(teacher));

        Teacher result = teacherService.findById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(teacherRepository, times(1)).findById(id);
    }

    @Test
    void findById_shouldReturnNullWhenNotFound() {
        Long id = 99L;

        when(teacherRepository.findById(id)).thenReturn(Optional.empty());

        Teacher result = teacherService.findById(id);

        assertNull(result);
        verify(teacherRepository, times(1)).findById(id);
    }
}
