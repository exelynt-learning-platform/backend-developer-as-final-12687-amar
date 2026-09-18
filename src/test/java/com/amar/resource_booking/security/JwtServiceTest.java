package com.amar.resource_booking.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

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

        org.junit.jupiter.api.Assertions.assertThrows(
                io.jsonwebtoken.ExpiredJwtException.class,
                () -> jwtService.isTokenValid(token, "admin")
        );
    }
}
