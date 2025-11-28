package com.openclassrooms.starterjwt.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.openclassrooms.starterjwt.unit.models.Session;
import com.openclassrooms.starterjwt.unit.models.Teacher;
import com.openclassrooms.starterjwt.unit.models.User;

import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SessionIntegrationIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper objectMapper;

    Long teacherId;
    Long sessionId;
    Long mainUserId;
    Long otherUserId;

    @BeforeEach
    void setup() {
        sessionRepository.deleteAll();
        teacherRepository.deleteAll();
        userRepository.deleteAll();

        // create teacher
        Teacher teacher = teacherRepository.save(
                Teacher.builder()
                        .firstName("Alan")
                        .lastName("Turing")
                        .build()
        );
        teacherId = teacher.getId();

        // main user for authentication
        User mainUser = userRepository.save(
                new User("john@example.com",
                        "Doe",
                        "John",
                        passwordEncoder.encode("secret123"),
                        false)
        );
        mainUserId = mainUser.getId();

        // second user
        User otherUser = userRepository.save(
                new User("alice@example.com",
                        "Smith",
                        "Alice",
                        passwordEncoder.encode("pass456"),
                        false)
        );
        otherUserId = otherUser.getId();

        // create a session
        Session s = sessionRepository.save(
                Session.builder()
                        .name("Yoga session")
                        .description("Relaxation training")
                        .date(new Date())
                        .teacher(teacher)
                        .build()
        );
        sessionId = s.getId();
    }

    // ----------------------------------------------------------------------
    // AUTH HELPER — get JWT
    // ----------------------------------------------------------------------
    private String loginAndGetToken() throws Exception {
        String json = """
            {
              "email":"john@example.com",
              "password":"secret123"
            }
            """;

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("token").asText();
    }

    // ----------------------------------------------------------------------
    // TESTS GET /api/session
    // ----------------------------------------------------------------------

    @Test
    void findAll_shouldReturnSessions() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(get("/api/session")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // ----------------------------------------------------------------------
    // TEST GET /api/session/{id}
    // ----------------------------------------------------------------------

    @Test
    void findById_shouldReturnSession() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(get("/api/session/" + sessionId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yoga session"));
    }

    @Test
    void findById_shouldReturnNotFound_whenSessionMissing() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(get("/api/session/99999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_shouldReturn400_whenIdInvalid() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(get("/api/session/abc")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    // ----------------------------------------------------------------------
    // TEST POST /api/session
    // ----------------------------------------------------------------------

    @Test
    void create_shouldCreateSession() throws Exception {
        String token = loginAndGetToken();

        String json = """
        {
            "name": "New Session",
            "description": "Test desc",
            "date": "2025-01-01T10:00:00",
            "teacher_id": %d,
            "users": []
        }
        """.formatted(teacherId);

        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Session"));

        assertEquals(2, sessionRepository.count());
    }

    // ----------------------------------------------------------------------
    // TEST PUT /api/session/{id}
    // ----------------------------------------------------------------------

    @Test
    void update_shouldModifySession() throws Exception {
        String token = loginAndGetToken();

        String json = """
        {
            "name": "Updated Session",
            "description": "Updated desc",
            "date": "2025-01-01T10:00:00",
            "teacher_id": %d,
            "users": []
        }
        """.formatted(teacherId);

        mockMvc.perform(put("/api/session/" + sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Session"));
    }

    // ----------------------------------------------------------------------
    // TEST DELETE /api/session/{id}
    // ----------------------------------------------------------------------

    @Test
    void delete_shouldRemoveSession() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(delete("/api/session/" + sessionId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        assertFalse(sessionRepository.existsById(sessionId));
    }

    @Test
    void delete_shouldReturn404_whenSessionMissing() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(delete("/api/session/99999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_shouldReturn400_whenIdInvalid() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(delete("/api/session/abc")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    // ----------------------------------------------------------------------
    // TEST PARTICIPATE
    // ----------------------------------------------------------------------

    @Test
    void participate_shouldAddUserToSession() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(post("/api/session/" + sessionId + "/participate/" + otherUserId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        Session updated = sessionRepository.findById(sessionId).get();
        assertEquals(1, updated.getUsers().size());
    }

    @Test
    void participate_shouldReturn404_whenUserMissing() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(post("/api/session/" + sessionId + "/participate/99999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void participate_shouldReturn400_whenAlreadyParticipating() throws Exception {
        String token = loginAndGetToken();

        // add user once
        mockMvc.perform(post("/api/session/" + sessionId + "/participate/" + otherUserId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // try again
        mockMvc.perform(post("/api/session/" + sessionId + "/participate/" + otherUserId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    // ----------------------------------------------------------------------
    // TEST NO LONGER PARTICIPATE
    // ----------------------------------------------------------------------

    @Test
    void noLongerParticipate_shouldRemoveUser() throws Exception {
        String token = loginAndGetToken();

        // add user
        mockMvc.perform(post("/api/session/" + sessionId + "/participate/" + otherUserId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // remove user
        mockMvc.perform(delete("/api/session/" + sessionId + "/participate/" + otherUserId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        Session updated = sessionRepository.findById(sessionId).get();
        assertTrue(updated.getUsers().isEmpty());
    }

    @Test
    void noLongerParticipate_shouldReturn400_whenNotParticipating() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(delete("/api/session/" + sessionId + "/participate/" + otherUserId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }
}
