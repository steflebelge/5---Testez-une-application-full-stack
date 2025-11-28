package com.openclassrooms.starterjwt.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.unit.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.unit.security.jwt.JwtUtils;

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

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JwtUtils jwtUtils;

    Long mainUserId;

    private static final String SECRET = "test-secret";

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        User u = userRepository.save(new User(
                "john@example.com",
                "Doe",
                "John",
                passwordEncoder.encode("secret123"),
                false
        ));
        mainUserId = u.getId();
    }

    // ----------------------------------------------------------------------
    // AUTH HELPER: login
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
    // SECURITY TESTS
    // ----------------------------------------------------------------------

    @Test
    void authEndpoints_shouldBeAccessibleWithoutToken() throws Exception {
        mockMvc.perform(post("/api/auth/login"))
                .andExpect(status().isBadRequest()); // 400 car données manquantes = OK
    }

    @Test
    void protectedEndpoint_shouldReturn401_withoutToken() throws Exception {
        mockMvc.perform(get("/api/user/" + mainUserId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_shouldReturn200_withValidToken() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(get("/api/user/" + mainUserId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpoint_shouldReturn401_withInvalidToken() throws Exception {
        mockMvc.perform(get("/api/user/" + mainUserId)
                        .header("Authorization", "Bearer INVALID.TOKEN"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void protectedEndpoint_shouldReturn401_withMalformedToken() throws Exception {
        mockMvc.perform(get("/api/user/" + mainUserId)
                        .header("Authorization", "Bearer "))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_shouldReturn401_withExpiredToken() throws Exception {
        String expired = Jwts.builder()
                .setSubject("john@example.com")
                .setExpiration(new Date(System.currentTimeMillis() - 60000)) // déjà expiré
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();

        mockMvc.perform(get("/api/user/" + mainUserId)
                        .header("Authorization", "Bearer " + expired))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_shouldReturn401_withTokenSignedWithWrongKey() throws Exception {
        String wrongToken = Jwts.builder()
                .setSubject("john@example.com")
                .setExpiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(SignatureAlgorithm.HS512, "WRONG_KEY")
                .compact();

        mockMvc.perform(get("/api/user/" + mainUserId)
                        .header("Authorization", "Bearer " + wrongToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void securityContext_shouldContainUserAfterValidJwt() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(get("/api/user/" + mainUserId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void unauthorizedHandler_shouldProduceStandardJson() throws Exception {
        mockMvc.perform(get("/api/session"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.path").exists());
    }
}
