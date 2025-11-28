package com.openclassrooms.starterjwt.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.unit.models.User;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserIntegrationIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper objectMapper;

    private Long userId;          // user principal
    private Long otherUserId;     // autre user pour tests 401

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @BeforeEach
    void setup() {
        sessionRepository.deleteAll();
        teacherRepository.deleteAll();
        userRepository.deleteAll();

        // utilisateur principal (login)
        User mainUser = userRepository.save(new User(
                "john@example.com",
                "Doe",
                "John",
                passwordEncoder.encode("secret123"),
                false
        ));
        userId = mainUser.getId();

        // second utilisateur pour test unauthorized delete
        User otherUser = userRepository.save(new User(
                "alice@example.com",
                "Alice",
                "Land",
                passwordEncoder.encode("pass456"),
                false
        ));
        otherUserId = otherUser.getId();
    }

    // ---------------------------------------------------------------------
    // HELPER — récupère un JWT valide pour john@example.com
    // ---------------------------------------------------------------------
    private String authenticateAndGetToken() throws Exception {
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

    // ---------------------------------------------------------------------
    // TEST: GET /api/user/{id}
    // ---------------------------------------------------------------------
    @Test
    void getUser_shouldReturnUser_whenExists() throws Exception {
        String token = authenticateAndGetToken();

        mockMvc.perform(get("/api/user/" + userId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void getUser_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        String token = authenticateAndGetToken();

        mockMvc.perform(get("/api/user/99999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUser_shouldReturnBadRequest_whenIdIsInvalid() throws Exception {
        String token = authenticateAndGetToken();

        mockMvc.perform(get("/api/user/abc")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    // ---------------------------------------------------------------------
    // TEST: DELETE /api/user/{id}
    // ---------------------------------------------------------------------
    @Test
    void deleteUser_shouldReturnOk_whenUserDeletesHimself() throws Exception {
        String token = authenticateAndGetToken();

        mockMvc.perform(delete("/api/user/" + userId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        assertFalse(userRepository.existsById(userId));
    }

    @Test
    void deleteUser_shouldReturnUnauthorized_whenConnectedUserIsDifferent() throws Exception {
        String token = authenticateAndGetToken();

        mockMvc.perform(delete("/api/user/" + otherUserId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteUser_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        String token = authenticateAndGetToken();

        mockMvc.perform(delete("/api/user/99999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_shouldReturnBadRequest_whenIdIsInvalid() throws Exception {
        String token = authenticateAndGetToken();

        mockMvc.perform(delete("/api/user/xyz")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }
}
