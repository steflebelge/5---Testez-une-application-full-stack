package com.openclassrooms.starterjwt.unit.security.jwt;

import com.openclassrooms.starterjwt.unit.security.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthTokenFilterTest {

    private AuthTokenFilter filter;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        filter = new AuthTokenFilter();

        // Injection manuelle car @Autowired ne fonctionne pas dans les tests unitaires
        filter.jwtUtils = jwtUtils;
        filter.userDetailsService = userDetailsService;

        // Nettoyage du SecurityContext
        SecurityContextHolder.clearContext();
    }

    // ---------------------------
    // parseJwt()
    // ---------------------------

    @Test
    void parseJwt_shouldReturnNullWhenNoHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        String token = invokeParseJwt(request);

        assertNull(token);
    }

    @Test
    void parseJwt_shouldReturnNullWhenHeaderDoesNotStartWithBearer() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Token abc123");

        String token = invokeParseJwt(request);

        assertNull(token);
    }

    @Test
    void parseJwt_shouldReturnTokenWhenBearerHeaderIsValid() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer abc123");

        String token = invokeParseJwt(request);

        assertEquals("abc123", token);
    }

    private String invokeParseJwt(MockHttpServletRequest request) {
        try {
            var method = AuthTokenFilter.class.getDeclaredMethod("parseJwt", javax.servlet.http.HttpServletRequest.class);
            method.setAccessible(true);
            return (String) method.invoke(filter, request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ---------------------------
    // doFilterInternal()
    // ---------------------------

    @Test
    void doFilterInternal_shouldDoNothingWhenJwtIsNull() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldDoNothingWhenTokenInvalid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalidtoken");

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtils.validateJwtToken("invalidtoken")).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldAuthenticateUserWhenTokenValid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer validtoken");

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtils.validateJwtToken("validtoken")).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken("validtoken")).thenReturn("john");

        UserDetails userDetails = new User("john", "pwd", Collections.emptyList());
        when(userDetailsService.loadUserByUsername("john")).thenReturn(userDetails);

        filter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("john", SecurityContextHolder.getContext().getAuthentication().getName());

        verify(userDetailsService).loadUserByUsername("john");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldCatchExceptionAndContinueFilter() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer xyz");

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtils.validateJwtToken("xyz")).thenThrow(new RuntimeException("BOOM"));

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}
