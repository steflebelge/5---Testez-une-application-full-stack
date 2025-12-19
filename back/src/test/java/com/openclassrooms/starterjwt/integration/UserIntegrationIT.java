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

import java.time.LocalDateTime;

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























    @Test
    void emptyConstructor_shouldInitAllFieldsToNull() {
        User u = new User();
        assertNull(u.getId());
        assertNull(u.getEmail());
        assertNull(u.getLastName());
        assertNull(u.getFirstName());
        assertNull(u.getPassword());
        assertFalse(u.isAdmin());
        assertNull(u.getCreatedAt());
        assertNull(u.getUpdatedAt());
    }

    @Test
    void constructorWithoutId_shouldAssignFields() {
        User u = new User(
                "john@example.com",
                "Doe",
                "John",
                "secret",
                true
        );

        assertEquals("john@example.com", u.getEmail());
        assertEquals("Doe", u.getLastName());
        assertEquals("John", u.getFirstName());
        assertEquals("secret", u.getPassword());
        assertTrue(u.isAdmin());
    }

    @Test
    void fullConstructor_shouldAssignAllFields() {
        LocalDateTime now = LocalDateTime.now();

        User u = new User(
                5L,
                "e@mail",
                "L",
                "F",
                "p",
                true,
                now,
                now
        );

        assertEquals(5L, u.getId());
        assertEquals("e@mail", u.getEmail());
        assertEquals("L", u.getLastName());
        assertEquals("F", u.getFirstName());
        assertEquals("p", u.getPassword());
        assertTrue(u.isAdmin());
        assertEquals(now, u.getCreatedAt());
        assertEquals(now, u.getUpdatedAt());
    }

    @Test
    void setters_shouldModifyValues() {
        User u = new User();

        LocalDateTime now = LocalDateTime.now();

        u.setId(10L);
        u.setEmail("a@b.com");
        u.setLastName("L");
        u.setFirstName("F");
        u.setPassword("pass");
        u.setAdmin(true);
        u.setCreatedAt(now);
        u.setUpdatedAt(now);

        assertEquals(10L, u.getId());
        assertEquals("a@b.com", u.getEmail());
        assertEquals("L", u.getLastName());
        assertEquals("F", u.getFirstName());
        assertEquals("pass", u.getPassword());
        assertTrue(u.isAdmin());
        assertEquals(now, u.getCreatedAt());
        assertEquals(now, u.getUpdatedAt());
    }

//    @Test
//    void equalsAndHashCode_shouldBeCorrect() {
//        User u1 = new User();
//        u1.setId(1L);
//
//        User u2 = new User();
//        u2.setId(1L);
//
//        assertEquals(u1, u2);
//        assertEquals(u1.hashCode(), u2.hashCode());
//        assertTrue(u1.canEqual(u2));
//    }
//
//    @Test
//    void equals_shouldReturnFalseWithDifferentTypes() {
//        User u = new User();
//        assertNotEquals(u, "string");
//        assertFalse(u.canEqual("string"));
//    }

    @Test
    void toString_shouldContainEmailWhenSet() {
        User u = new User();
        u.setEmail("hello@mail.com");
        assertTrue(u.toString().contains("hello@mail.com"));
    }
}
