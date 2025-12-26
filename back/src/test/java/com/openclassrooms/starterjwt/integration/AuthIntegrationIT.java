package com.openclassrooms.starterjwt.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.unit.models.User;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationIT {

    @Autowired
    SessionRepository sessionRepository;
    @Autowired
    TeacherRepository teacherRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        sessionRepository.deleteAll();
        teacherRepository.deleteAll();
        userRepository.deleteAll();
    }

    // ----------------------------------------------------------------------
    // REGISTER TESTS
    // ----------------------------------------------------------------------

    @Test
    void register_shouldCreateUser() throws Exception {

        String json = """
                {
                    "email": "john@example.com",
                    "firstName": "John",
                    "lastName": "Doe",
                    "password": "secret123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));

        assertEquals(1, userRepository.count());
    }

    @Test
    void register_shouldReturnBadRequest_whenEmailAlreadyExists() throws Exception {

        userRepository.save(new User(
                "john@example.com",
                "Doe",
                "John",
                passwordEncoder.encode("123456"),
                false
        ));

        String json = """
                {
                    "email": "john@example.com",
                    "firstName": "John",
                    "lastName": "Doe",
                    "password": "secret123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already taken!"));
    }


    @Test
    void register_shouldReturn400_whenEmailIsMissing() throws Exception {
        String json = """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "password": "secret123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }


    @Test
    void register_shouldReturn400_whenEmailIsInvalid() throws Exception {
        String json = """
                {
                    "email": "not-an-email",
                    "firstName": "John",
                    "lastName": "Doe",
                    "password": "secret123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }


    @Test
    void register_shouldReturn400_whenJsonIsMalformed() throws Exception {
        String json = """
                {
                    "email": "john@example.com",
                    "firstName": "John",
                    "lastName": "Doe",
                    "password": "secret123"
                """; // accolade manquante

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }


    // ----------------------------------------------------------------------
    // LOGIN TESTS
    // ----------------------------------------------------------------------

    @Test
    void login_shouldReturnJwt_whenCredentialsAreValid() throws Exception {

        // ajouter user dans base
        userRepository.save(new User(
                "john@example.com",
                "Doe",
                "John",
                passwordEncoder.encode("secret123"),
                false
        ));

        String json = """
                {
                    "email": "john@example.com",
                    "password": "secret123"
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("john@example.com"))
                .andExpect(jsonPath("$.admin").value(false))
                .andReturn();

        // vérifier que le token n'est pas vide
        String response = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(response).get("token").textValue();

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void login_shouldFail_whenPasswordIsWrong() throws Exception {

        userRepository.save(new User(
                "john@example.com",
                "Doe",
                "John",
                passwordEncoder.encode("secret123"),
                false
        ));

        String json = """
                {
                    "email": "john@example.com",
                    "password": "WRONG"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_shouldFail_whenUserDoesNotExist() throws Exception {

        String json = """
                {
                    "email": "nope@example.com",
                    "password": "123456"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_shouldHandleMissingUserGracefully() throws Exception {
        // on ne crée pas l'utilisateur dans la base
        String json = """
                {
                    "email": "ghost@example.com",
                    "password": "any"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_shouldReturnCorrectAdminStatus() throws Exception {
        // ajouter user dans base
        userRepository.save(new User(
                "john@example.com",
                "Doe",
                "John",
                passwordEncoder.encode("secret123"),
                true
        ));

        String json = """
                {
                    "email": "john@example.com",
                    "password": "secret123"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.admin").value(true));
    }
}
