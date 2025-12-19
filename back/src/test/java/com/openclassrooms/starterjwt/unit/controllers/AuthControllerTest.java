package com.openclassrooms.starterjwt.unit.controllers;

import com.openclassrooms.starterjwt.unit.models.User;
import com.openclassrooms.starterjwt.unit.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.unit.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.unit.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.unit.payload.response.MessageResponse;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.unit.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.unit.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private AuthController authController;
    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;
    private JwtUtils jwtUtils;
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        authenticationManager = mock(AuthenticationManager.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtUtils = mock(JwtUtils.class);
        userRepository = mock(UserRepository.class);

        authController = new AuthController(authenticationManager, passwordEncoder, jwtUtils, userRepository);
    }

    // -------------------------------
    // REGISTER
    // -------------------------------

    @Test
    void register_shouldReturnBadRequest_whenEmailExists() {
        SignupRequest req = new SignupRequest();
        req.setEmail("john@example.com");

        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        ResponseEntity<?> response = authController.registerUser(req);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof MessageResponse);
        assertEquals("Error: Email is already taken!", ((MessageResponse) response.getBody()).getMessage());
    }

    @Test
    void register_shouldCreateUser_whenEmailDoesNotExist() {
        SignupRequest req = new SignupRequest();
        req.setEmail("john@example.com");
        req.setFirstName("John");
        req.setLastName("Doe");
        req.setPassword("secret");

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("encoded-secret");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        ResponseEntity<?> response = authController.registerUser(req);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof MessageResponse);
        assertEquals("User registered successfully!", ((MessageResponse) response.getBody()).getMessage());

        // vérifier que le mot de passe a été encodé
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("encoded-secret", captor.getValue().getPassword());
    }

    // -------------------------------
    // LOGIN
    // -------------------------------

    @Test
    void authenticateUser_shouldReturnJwt_whenUserExists() {
        LoginRequest req = new LoginRequest();
        req.setEmail("john@example.com");
        req.setPassword("secret");

        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl principal = UserDetailsImpl.builder()
                .id(1L)
                .username("john@example.com")
                .firstName("John")
                .lastName("Doe")
                .admin(false)
                .password("secret")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(principal);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("JWT-TOKEN");

        User userFromRepo = new User("john@example.com", "Doe", "John", "secret", false);
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(userFromRepo));

        ResponseEntity<?> response = authController.authenticateUser(req);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof JwtResponse);
        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertEquals("JWT-TOKEN", jwtResponse.getToken());
        assertEquals("john@example.com", jwtResponse.getUsername());
        assertNotEquals(true, (boolean) jwtResponse.getAdmin());
    }


    @Test
    void authenticateUser_shouldReturnJwt_whenUserIsNull() {
        // Cette branche ne peut pas arriver en vrai avec Spring Security,
        // mais en test unitaire on peut mocker UserRepository pour renvoyer null.
        LoginRequest req = new LoginRequest();
        req.setEmail("john@example.com");
        req.setPassword("secret");

        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl principal = UserDetailsImpl.builder()
                .id(1L)
                .username("john@example.com")
                .firstName("John")
                .lastName("Doe")
                .admin(false)
                .password("secret")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(principal);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("JWT-TOKEN");

        // simulate userRepository.findByEmail() -> empty
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.authenticateUser(req);

        assertEquals(200, response.getStatusCodeValue());
        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertNotNull(jwtResponse);
        // admin doit rester false car user == null
        assertNotEquals(true, (boolean) jwtResponse.getAdmin());

    }
}
