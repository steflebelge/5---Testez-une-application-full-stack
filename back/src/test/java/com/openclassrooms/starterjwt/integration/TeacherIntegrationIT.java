package com.openclassrooms.starterjwt.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.unit.models.Teacher;
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
import com.openclassrooms.starterjwt.repository.SessionRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TeacherIntegrationIT {
    private Long teacherId;
    private Long loginUserId;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    TeacherRepository teacherRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    SessionRepository sessionRepository;


    @BeforeEach
    void setup() {
        sessionRepository.deleteAll();
        teacherRepository.deleteAll();
        userRepository.deleteAll();

        Teacher teacher = teacherRepository.save(Teacher.builder()
                .firstName("John")
                .lastName("Doe")
                .build());
        teacherId = teacher.getId();

        // second teacher pour testé la liste
        teacherRepository.save(Teacher.builder()
                .firstName("Anna")
                .lastName("Smith")
                .build());

        // user pour login
        User loginUser = userRepository.save(
                new User(
                        "john@example.com",
                        "Doe",
                        "John",
                        passwordEncoder.encode("secret123"),
                        false
                )
        );
        loginUserId = loginUser.getId();
    }

    private String loginAndGetToken() throws Exception {
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
                .andReturn();

        return objectMapper.readTree(
                result.getResponse().getContentAsString()
        ).get("token").asText();
    }

    // ---------------------------------------------------------------------
    // TEST: GET /api/teacher/{id}
    // ---------------------------------------------------------------------
    @Test
    void getTeacherById_shouldReturnTeacher_whenExists() throws Exception {
        String token = loginAndGetToken();
        mockMvc.perform(get("/api/teacher/" + teacherId).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(teacherId))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void getTeacherById_shouldReturnNotFound_whenDoesNotExist() throws Exception {
        String token = loginAndGetToken();
        mockMvc.perform(get("/api/teacher/99999").header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTeacherById_shouldReturnBadRequest_whenIdIsInvalid() throws Exception {
        String token = loginAndGetToken();
        mockMvc.perform(get("/api/teacher/abc").header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    // ---------------------------------------------------------------------
    // TEST: GET /api/teacher (liste)
    // ---------------------------------------------------------------------
    @Test
    void getAllTeachers_shouldReturnList() throws Exception {
        String token = loginAndGetToken();
        mockMvc.perform(get("/api/teacher").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").exists())
                .andExpect(jsonPath("$[1].firstName").exists());
    }


    // ---------------------------------------------------------------------
    // TEST: Security
    // ---------------------------------------------------------------------

    @Test
    void getTeacher_shouldReturn401_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/teacher/" + teacherId))
                .andExpect(status().isUnauthorized());
    }
}
