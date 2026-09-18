package com.amar.resource_booking.controller;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.amar.resource_booking.dto.LoginRequest;
import com.amar.resource_booking.dto.LoginResponse;
import com.amar.resource_booking.service.AuthService;

class AuthControllerTest {

    private AuthService authService;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        authController = new AuthController(authService);
    }

    @Test
    void loginShouldReturnLoginResponse() {

        LoginRequest request = mock(LoginRequest.class);
        LoginResponse expectedResponse = mock(LoginResponse.class);

        when(authService.login(request))
                .thenReturn(expectedResponse);

        LoginResponse response =
                authController.login(request);

        assertSame(expectedResponse, response);
    }
}