package com.amar.resource_booking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.amar.resource_booking.dto.LoginRequest;
import com.amar.resource_booking.dto.LoginResponse;
import com.amar.resource_booking.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginShouldReturnToken() {

        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin");

        when(jwtService.generateToken("admin"))
                .thenReturn("test-jwt-token");

        LoginResponse response =
                authService.login(request);

        assertNotNull(response);
        assertEquals("test-jwt-token", response.getToken());

        verify(authenticationManager).authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        );

        verify(jwtService).generateToken("admin");
    }
    @Test
    void loginShouldThrowExceptionWhenAuthenticationFails() {

        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("wrong");

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenThrow(
                new org.springframework.security.authentication.BadCredentialsException(
                        "Invalid username or password"
                )
        );

        org.junit.jupiter.api.Assertions.assertThrows(
                org.springframework.security.authentication.BadCredentialsException.class,
                () -> authService.login(request)
        );

        verify(authenticationManager).authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        );
    }
}