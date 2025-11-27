package com.openclassrooms.starterjwt.unit.controllers;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.unit.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.unit.models.Teacher;
import com.openclassrooms.starterjwt.unit.services.TeacherService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeacherControllerTest {

    @Mock
    private TeacherService teacherService;

    @Mock
    private TeacherMapper teacherMapper;

    private TeacherController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new TeacherController(teacherService, teacherMapper);
    }

    // --------------------------------------------------------------------
    // GET /api/teacher/{id}
    // --------------------------------------------------------------------

    @Test
    void findById_shouldReturnOk_whenTeacherFound() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);

        TeacherDto dto = new TeacherDto();

        when(teacherService.findById(1L)).thenReturn(teacher);
        when(teacherMapper.toDto(teacher)).thenReturn(dto);

        ResponseEntity<?> response = controller.findById("1");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    void findById_shouldReturnNotFound_whenTeacherNotFound() {
        when(teacherService.findById(1L)).thenReturn(null);

        ResponseEntity<?> response = controller.findById("1");

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void findById_shouldReturnBadRequest_whenIdNotNumeric() {
        ResponseEntity<?> response = controller.findById("abc");

        assertEquals(400, response.getStatusCodeValue());
        verify(teacherService, never()).findById(anyLong());
    }

    // --------------------------------------------------------------------
    // GET /api/teacher
    // --------------------------------------------------------------------

    @Test
    void findAll_shouldReturnTeacherList() {
        List<Teacher> teachers = Arrays.asList(new Teacher(), new Teacher());
        List<TeacherDto> dtoList = Arrays.asList(new TeacherDto(), new TeacherDto());

        when(teacherService.findAll()).thenReturn(teachers);
        when(teacherMapper.toDto(teachers)).thenReturn(dtoList);

        ResponseEntity<?> response = controller.findAll();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dtoList, response.getBody());
    }
}
