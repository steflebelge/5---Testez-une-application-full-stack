package com.openclassrooms.starterjwt.unit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AuthEntryPointJwtTest {

    @Test
    void commence_shouldReturn401JsonResponse() throws IOException, ServletException {
        AuthEntryPointJwt entryPoint = new AuthEntryPointJwt();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/test");

        MockHttpServletResponse response = new MockHttpServletResponse();

        AuthenticationException authException = new AuthenticationException("Bad credentials") {};

        entryPoint.commence(request, response, authException);

        assertEquals(401, response.getStatus());
        assertEquals("application/json", response.getContentType());

        ObjectMapper mapper = new ObjectMapper();
        Map<?, ?> json = mapper.readValue(response.getContentAsByteArray(), Map.class);

        assertEquals(401, json.get("status"));
        assertEquals("Unauthorized", json.get("error"));
        assertEquals("Bad credentials", json.get("message"));
        assertEquals("/api/test", json.get("path"));
    }
}
