package com.openclassrooms.starterjwt.unit.controllers;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.unit.mapper.SessionMapper;
import com.openclassrooms.starterjwt.unit.models.Session;
import com.openclassrooms.starterjwt.unit.services.SessionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SessionControllerTest {

    @Mock
    private SessionService sessionService;

    @Mock
    private SessionMapper sessionMapper;

    private SessionController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new SessionController(sessionService, sessionMapper);
    }

    // --------------------------------------------------------------------
    // GET /api/session/{id}
    // --------------------------------------------------------------------

    @Test
    void findById_shouldReturnOk_whenSessionExists() {
        Session session = new Session();
        session.setId(1L);

        SessionDto dto = new SessionDto();
        dto.setId(1L);

        when(sessionService.getById(1L)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(dto);

        ResponseEntity<?> response = controller.findById("1");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    void findById_shouldReturnNotFound_whenSessionDoesNotExist() {
        when(sessionService.getById(1L)).thenReturn(null);

        ResponseEntity<?> response = controller.findById("1");

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void findById_shouldReturnBadRequest_whenIdNotNumber() {
        ResponseEntity<?> response = controller.findById("abc");

        assertEquals(400, response.getStatusCodeValue());
    }

    // --------------------------------------------------------------------
    // GET /api/session
    // --------------------------------------------------------------------

    @Test
    void findAll_shouldReturnListOfSessions() {
        List<Session> sessions = Arrays.asList(new Session(), new Session());
        List<SessionDto> dtos = Arrays.asList(new SessionDto(), new SessionDto());

        when(sessionService.findAll()).thenReturn(sessions);
        when(sessionMapper.toDto(sessions)).thenReturn(dtos);

        ResponseEntity<?> response = controller.findAll();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dtos, response.getBody());
    }

    // --------------------------------------------------------------------
    // POST /api/session
    // --------------------------------------------------------------------

    @Test
    void create_shouldMapAndCreateSession() {
        SessionDto dto = new SessionDto();
        Session session = new Session();
        Session saved = new Session();
        saved.setId(1L);
        SessionDto savedDto = new SessionDto();
        savedDto.setId(1L);

        when(sessionMapper.toEntity(dto)).thenReturn(session);
        when(sessionService.create(session)).thenReturn(saved);
        when(sessionMapper.toDto(saved)).thenReturn(savedDto);

        ResponseEntity<?> response = controller.create(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(savedDto, response.getBody());
    }

    // --------------------------------------------------------------------
    // PUT /api/session/{id}
    // --------------------------------------------------------------------

    @Test
    void update_shouldReturnOk_whenValidId() {
        SessionDto dto = new SessionDto();
        Session session = new Session();
        session.setId(1L);
        SessionDto sessionDto = new SessionDto();
        sessionDto.setId(1L);

        when(sessionMapper.toEntity(dto)).thenReturn(session);
        when(sessionService.update(1L, session)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        ResponseEntity<?> response = controller.update("1", dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(sessionDto, response.getBody());
    }

    @Test
    void update_shouldReturnBadRequest_whenIdNotNumeric() {
        ResponseEntity<?> response = controller.update("abc", new SessionDto());

        assertEquals(400, response.getStatusCodeValue());
    }

    // --------------------------------------------------------------------
    // DELETE /api/session/{id}
    // --------------------------------------------------------------------

    @Test
    void delete_shouldReturnOk_whenSessionExists() {
        Session session = new Session();
        when(sessionService.getById(1L)).thenReturn(session);

        ResponseEntity<?> response = controller.save("1");

        assertEquals(200, response.getStatusCodeValue());
        verify(sessionService).delete(1L);
    }

    @Test
    void delete_shouldReturnNotFound_whenSessionDoesNotExist() {
        when(sessionService.getById(1L)).thenReturn(null);

        ResponseEntity<?> response = controller.save("1");

        assertEquals(404, response.getStatusCodeValue());
        verify(sessionService, never()).delete(anyLong());
    }

    @Test
    void delete_shouldReturnBadRequest_whenIdNotNumeric() {
        ResponseEntity<?> response = controller.save("abc");

        assertEquals(400, response.getStatusCodeValue());
    }

    // --------------------------------------------------------------------
    // POST /api/session/{id}/participate/{userId}
    // --------------------------------------------------------------------

    @Test
    void participate_shouldReturnOk_whenInputValid() {
        ResponseEntity<?> response = controller.participate("1", "2");

        assertEquals(200, response.getStatusCodeValue());
        verify(sessionService).participate(1L, 2L);
    }

    @Test
    void participate_shouldReturnBadRequest_whenIdsNotNumeric() {
        ResponseEntity<?> response = controller.participate("aaa", "2");

        assertEquals(400, response.getStatusCodeValue());
        verify(sessionService, never()).participate(anyLong(), anyLong());
    }

    // --------------------------------------------------------------------
    // DELETE /api/session/{id}/participate/{userId}
    // --------------------------------------------------------------------

    @Test
    void noLongerParticipate_shouldReturnOk_whenInputValid() {
        ResponseEntity<?> response = controller.noLongerParticipate("1", "2");

        assertEquals(200, response.getStatusCodeValue());
        verify(sessionService).noLongerParticipate(1L, 2L);
    }

    @Test
    void noLongerParticipate_shouldReturnBadRequest_whenIdsNotNumeric() {
        ResponseEntity<?> response = controller.noLongerParticipate("abc", "xyz");

        assertEquals(400, response.getStatusCodeValue());
        verify(sessionService, never()).noLongerParticipate(anyLong(), anyLong());
    }
}
