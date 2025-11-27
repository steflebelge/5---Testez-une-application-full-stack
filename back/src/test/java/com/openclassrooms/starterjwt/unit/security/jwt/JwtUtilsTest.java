package com.openclassrooms.starterjwt.unit.security.jwt;

import com.openclassrooms.starterjwt.unit.security.services.UserDetailsImpl;

import io.jsonwebtoken.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.security.core.Authentication;

import java.lang.reflect.Field;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    private final String SECRET = "mySuperSecretKey123456789";
    private final int EXPIRATION_MS = 3600000;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        jwtUtils = new JwtUtils();

        injectPrivateField(jwtUtils, "jwtSecret", SECRET);
        injectPrivateField(jwtUtils, "jwtExpirationMs", EXPIRATION_MS);
    }

    private void injectPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }

    // ----------------------------------------------------
    // generateJwtToken()
    // ----------------------------------------------------

    @Test
    void generateJwtToken_shouldReturnValidToken() {
        UserDetailsImpl user = new UserDetailsImpl(
                1L, "john@example.com", "John", "Doe", false, "pwd"
        );

        when(authentication.getPrincipal()).thenReturn(user);

        String token = jwtUtils.generateJwtToken(authentication);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    // ----------------------------------------------------
    // getUserNameFromJwtToken()
    // ----------------------------------------------------

    @Test
    void getUserNameFromJwtToken_shouldReturnUsername() {
        String token = Jwts.builder()
                .setSubject("john@example.com")
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();

        String username = jwtUtils.getUserNameFromJwtToken(token);

        assertEquals("john@example.com", username);
    }

    // ----------------------------------------------------
    // validateJwtToken() - valid token
    // ----------------------------------------------------

    @Test
    void validateJwtToken_shouldReturnTrueForValidToken() {
        String token = Jwts.builder()
                .setSubject("john@example.com")
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();

        assertTrue(jwtUtils.validateJwtToken(token));
    }

    // ----------------------------------------------------
    // validateJwtToken() - exceptions
    // ----------------------------------------------------

    @Test
    void validateJwtToken_shouldReturnFalseForSignatureException() {
        String token = "invalid.signature.token";

        assertFalse(jwtUtils.validateJwtToken(token));
    }

    @Test
    void validateJwtToken_shouldReturnFalseForMalformedJwtException() {
        String token = "totallyInvalidTokenWithoutParts";
        assertFalse(jwtUtils.validateJwtToken(token));
    }

    @Test
    void validateJwtToken_shouldReturnFalseForExpiredJwtException() {
        String expiredToken = Jwts.builder()
                .setSubject("expired@example.com")
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // déjà expiré
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();

        assertFalse(jwtUtils.validateJwtToken(expiredToken));
    }

    @Test
    void validateJwtToken_shouldReturnFalseForUnsupportedJwtException() {
        // JWS in an unsupported format → trigger UnsupportedJwtException
        String token = Jwts.builder()
                .setSubject("john@example.com")
                .setHeaderParam("alg", "none")  // alg=none interdit
                .compact();

        assertFalse(jwtUtils.validateJwtToken(token));
    }

    @Test
    void validateJwtToken_shouldReturnFalseForIllegalArgumentException() {
        assertFalse(jwtUtils.validateJwtToken(""));
        assertFalse(jwtUtils.validateJwtToken(null));
    }
}
