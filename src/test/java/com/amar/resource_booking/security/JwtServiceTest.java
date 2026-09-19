package com.amar.resource_booking.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.ExpiredJwtException;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {

        jwtService = new JwtService();

        ReflectionTestUtils.setField(
                jwtService,
                "secretKey",
                "TestSecretKeyForResourceBookingApplication123456789"
        );

        ReflectionTestUtils.setField(
                jwtService,
                "expirationTime",
                3600000L
        );
        ReflectionTestUtils.setField(
                jwtService,
                "issuer",
                "resource-booking-api"
        );

        ReflectionTestUtils.setField(
                jwtService,
                "audience",
                "resource-booking-client"
        );
    }

    @Test
    void generateTokenShouldCreateValidToken() {

        String token = jwtService.generateToken("admin");

        assertNotNull(token);
        assertEquals("admin", jwtService.extractUsername(token));
    }

    @Test
    void isTokenValidShouldReturnTrueForValidToken() {

        String token = jwtService.generateToken("admin");

        boolean result =
                jwtService.isTokenValid(token, "admin");

        assertEquals(true, result);
    }

    @Test
    void isTokenValidShouldThrowExceptionForExpiredToken() {

        ReflectionTestUtils.setField(
                jwtService,
                "expirationTime",
                0L
        );

        String token = jwtService.generateToken("admin");

        assertThrows(
                ExpiredJwtException.class,
                () -> jwtService.isTokenValid(token, "admin")
        );
    }
    
    @Test
    void isTokenValidShouldReturnFalseForDifferentUsername() {

        String token = jwtService.generateToken("admin");

        boolean result =
                jwtService.isTokenValid(token, "user");

        assertEquals(false, result);
    }
}