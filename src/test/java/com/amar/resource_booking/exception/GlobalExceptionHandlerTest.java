package com.amar.resource_booking.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import org.springframework.mock.web.MockHttpServletRequest;

import jakarta.validation.ConstraintViolationException;

import org.springframework.security.authentication.BadCredentialsException;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleValidationExceptionShouldReturnBadRequest() {

        MethodArgumentNotValidException exception =
                mock(MethodArgumentNotValidException.class);

        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError =
                new FieldError(
                        "resourceRequest",
                        "name",
                        "Name is required"
                );

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors())
                .thenReturn(java.util.List.of(fieldError));

        ResponseEntity<Map<String, String>> response =
                exceptionHandler.handleValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(
                "Name is required",
                response.getBody().get("name")
        );
    }

    @Test
    void handleResourceNotFoundShouldReturnNotFound() {

        ResourceNotFoundException exception =
                new ResourceNotFoundException("Resource not found");

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setRequestURI("/api/resources/99");

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleResourceNotFound(
                        exception,
                        request
                );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(
                "Resource not found",
                response.getBody().getMessage()
        );
        assertEquals(
                "/api/resources/99",
                response.getBody().getPath()
        );
    }

    @Test
    void handleForbiddenShouldReturnForbidden() {

        ForbiddenException exception =
                new ForbiddenException("Access denied");

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setRequestURI("/api/reservations/1");

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleForbidden(
                        exception,
                        request
                );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(
                "Access denied",
                response.getBody().getMessage()
        );
        assertEquals(
                "/api/reservations/1",
                response.getBody().getPath()
        );
    }

    @Test
    void handleBadRequestShouldReturnBadRequest() {

        BadRequestException exception =
                new BadRequestException("Invalid reservation");

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setRequestURI("/api/reservations");

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleBadRequest(
                        exception,
                        request
                );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(
                "Invalid reservation",
                response.getBody().getMessage()
        );
        assertEquals(
                "/api/reservations",
                response.getBody().getPath()
        );
    }

    @Test
    void handleBadCredentialsShouldReturnUnauthorized() {

        BadCredentialsException exception =
                new BadCredentialsException("Bad credentials");

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setRequestURI("/auth/login");

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleBadCredentials(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                "Invalid username or password",
                response.getBody().getMessage()
        );

        assertEquals(
                "/auth/login",
                response.getBody().getPath()
        );
    }

    @Test
    void handleConstraintViolationShouldReturnBadRequest() {

        ConstraintViolationException exception =
                mock(ConstraintViolationException.class);

        when(exception.getMessage())
                .thenReturn("Page cannot be negative");

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setRequestURI("/api/reservations");

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleConstraintViolation(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                "Page cannot be negative",
                response.getBody().getMessage()
        );

        assertEquals(
                "/api/reservations",
                response.getBody().getPath()
        );
    }
}