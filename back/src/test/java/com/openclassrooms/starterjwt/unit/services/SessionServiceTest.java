package com.openclassrooms.starterjwt.unit.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.unit.models.Session;
import com.openclassrooms.starterjwt.unit.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class SessionServiceTest {

    private SessionRepository sessionRepository;
    private UserRepository userRepository;
    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        sessionRepository = mock(SessionRepository.class);
        userRepository = mock(UserRepository.class);
        sessionService = new SessionService(sessionRepository, userRepository);
    }

    @Test
    void create_shouldSaveSession() {
        Session session = new Session();
        when(sessionRepository.save(session)).thenReturn(session);

        Session result = sessionService.create(session);

        assertEquals(session, result);
        verify(sessionRepository).save(session);
    }

    @Test
    void delete_shouldCallRepositoryDelete() {
        Long id = 1L;

        sessionService.delete(id);

        verify(sessionRepository, times(1)).deleteById(id);
    }

    @Test
    void findAll_shouldReturnSessionList() {
        List<Session> list = Arrays.asList(new Session(), new Session());
        when(sessionRepository.findAll()).thenReturn(list);

        List<Session> result = sessionService.findAll();

        assertEquals(2, result.size());
        verify(sessionRepository).findAll();
    }

    @Test
    void getById_shouldReturnSessionWhenFound() {
        Long id = 10L;
        Session s = new Session();
        s.setId(id);

        when(sessionRepository.findById(id)).thenReturn(Optional.of(s));

        Session result = sessionService.getById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(sessionRepository).findById(id);
    }

    @Test
    void getById_shouldReturnNullWhenNotFound() {
        Long id = 10L;

        when(sessionRepository.findById(id)).thenReturn(Optional.empty());

        Session result = sessionService.getById(id);

        assertNull(result);
        verify(sessionRepository).findById(id);
    }

    @Test
    void update_shouldSetIdAndSave() {
        Long id = 5L;
        Session session = new Session();

        when(sessionRepository.save(session)).thenReturn(session);

        Session result = sessionService.update(id, session);

        assertEquals(id, session.getId());
        assertEquals(session, result);
        verify(sessionRepository).save(session);
    }

    // ----------------------------
    // participate()
    // ----------------------------

    @Test
    void participate_shouldAddUserToSession() {
        Long sessionId = 1L;
        Long userId = 2L;

        Session session = new Session();
        session.setId(sessionId);
        session.setUsers(new ArrayList<>());

        User user = new User();
        user.setId(userId);

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        sessionService.participate(sessionId, userId);

        assertEquals(1, session.getUsers().size());
        assertEquals(user, session.getUsers().get(0));
        verify(sessionRepository).save(session);
    }

    @Test
    void participate_shouldThrowNotFoundIfSessionNotFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));

        assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 2L));
    }

    @Test
    void participate_shouldThrowNotFoundIfUserNotFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(new Session()));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 2L));
    }

    @Test
    void participate_shouldThrowBadRequestIfAlreadyParticipating() {
        Long sessionId = 1L;
        Long userId = 2L;

        User user = new User();
        user.setId(userId);

        Session session = new Session();
        session.setUsers(new ArrayList<>(Collections.singletonList(user)));

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> sessionService.participate(sessionId, userId));
    }

    // ----------------------------
    // noLongerParticipate()
    // ----------------------------

    @Test
    void noLongerParticipate_shouldRemoveUserFromSession() {
        Long sessionId = 1L;
        Long userId = 2L;

        User user = new User();
        user.setId(userId);

        Session session = new Session();
        session.setUsers(new ArrayList<>(Collections.singletonList(user)));

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        sessionService.noLongerParticipate(sessionId, userId);

        assertTrue(session.getUsers().isEmpty());
        verify(sessionRepository).save(session);
    }

    @Test
    void noLongerParticipate_shouldThrowNotFoundIfSessionNotFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.noLongerParticipate(1L, 99L));
    }

    @Test
    void noLongerParticipate_shouldThrowBadRequestIfUserNotParticipating() {
        Long sessionId = 1L;
        Long userId = 2L;

        Session session = new Session();
        session.setUsers(new ArrayList<>()); // empty

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        assertThrows(BadRequestException.class, () -> sessionService.noLongerParticipate(sessionId, userId));
    }
}
