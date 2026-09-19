package com.amar.resource_booking.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.amar.resource_booking.entity.Role;
import com.amar.resource_booking.entity.User;
import com.amar.resource_booking.repository.UserRepository;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;

class JwtAuthenticationFilterTest {

    private JwtService jwtService;
    private UserRepository userRepository;
    private JwtAuthenticationFilter filter;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {

        jwtService = new JwtService();

        org.springframework.test.util.ReflectionTestUtils.setField(
                jwtService,
                "secretKey",
                "TestSecretKeyForResourceBookingApplication123456789"
        );

        org.springframework.test.util.ReflectionTestUtils.setField(
                jwtService,
                "expirationTime",
                3600000L
        );

        org.springframework.test.util.ReflectionTestUtils.setField(
                jwtService,
                "issuer",
                "resource-booking-api"
        );

        org.springframework.test.util.ReflectionTestUtils.setField(
                jwtService,
                "audience",
                "resource-booking-client"
        );
        userRepository = mock(UserRepository.class);

        filter = new JwtAuthenticationFilter(
                jwtService,
                userRepository
        );

        filterChain = mock(FilterChain.class);

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void validTokenShouldSetAuthentication() throws Exception {

        String token = jwtService.generateToken("admin");

        User user = new User(
                "admin",
                "password",
                Role.ADMIN
        );

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(user));

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setRequestURI("/api/resources");

        request.addHeader(
                "Authorization",
                "Bearer " + token
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertNotNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        assertEquals(
                "admin",
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName()
        );

        assertEquals(
                List.of(
                        new SimpleGrantedAuthority("ROLE_ADMIN")
                ),
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getAuthorities()
        );

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void requestWithoutAuthorizationHeaderShouldContinueFilterChain()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setRequestURI("/api/resources");

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void invalidTokenShouldReturnUnauthorized() throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setRequestURI("/api/resources");

        request.addHeader(
                "Authorization",
                "Bearer invalid-token"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertEquals(
                401,
                response.getStatus()
        );

        verify(filterChain, never())
                .doFilter(request, response);
    }

    @Test
    void unknownUserShouldContinueWithoutAuthentication()
            throws Exception {

        String token = jwtService.generateToken("unknown");

        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setRequestURI("/api/resources");

        request.addHeader(
                "Authorization",
                "Bearer " + token
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void loginRequestShouldNotBeFiltered() throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setRequestURI("/auth/login");

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void swaggerRequestShouldNotBeFiltered() throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setRequestURI("/swagger-ui/index.html");

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verify(filterChain).doFilter(request, response);
    }
}