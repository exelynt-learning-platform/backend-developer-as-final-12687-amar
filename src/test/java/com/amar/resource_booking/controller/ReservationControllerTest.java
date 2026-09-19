package com.amar.resource_booking.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.amar.resource_booking.dto.ReservationRequest;
import com.amar.resource_booking.dto.ReservationResponse;
import com.amar.resource_booking.entity.ReservationStatus;
import com.amar.resource_booking.service.ReservationService;
import com.amar.resource_booking.dto.ReservationFilterRequest;

class ReservationControllerTest {

    private ReservationService reservationService;
    private ReservationController reservationController;

    @BeforeEach
    void setUp() {
        reservationService = mock(ReservationService.class);
        reservationController =
                new ReservationController(reservationService);
    }

    @Test
    void createReservationShouldReturnCreated() {

        ReservationRequest request =
                mock(ReservationRequest.class);

        ReservationResponse expectedResponse =
                mock(ReservationResponse.class);

        Authentication authentication =
                mock(Authentication.class);

        when(authentication.getName())
                .thenReturn("user");

        when(reservationService.createReservation(
                request,
                "user"
        )).thenReturn(expectedResponse);

        ResponseEntity<ReservationResponse> response =
                reservationController.createReservation(
                        request,
                        authentication
                );

        assertEquals(
                HttpStatus.CREATED,
                response.getStatusCode()
        );

        assertSame(
                expectedResponse,
                response.getBody()
        );
    }

    @Test
    void getMyReservationsShouldReturnOk() {

        Authentication authentication =
                mock(Authentication.class);

        when(authentication.getName())
                .thenReturn("user");

        ReservationFilterRequest filter =
                new ReservationFilterRequest();

        filter.setStatus(ReservationStatus.CONFIRMED);
        filter.setMinPrice(new BigDecimal("100"));
        filter.setMaxPrice(new BigDecimal("1000"));
        filter.setPage(0);
        filter.setSize(5);
        filter.setSortBy("price");
        filter.setSortDirection("desc");

        Page<ReservationResponse> expectedPage =
                mock(Page.class);

        when(reservationService.getMyReservations(
                "user",
                ReservationStatus.CONFIRMED,
                new BigDecimal("100"),
                new BigDecimal("1000"),
                0,
                5,
                "price",
                "desc"
        )).thenReturn(expectedPage);

        ResponseEntity<Page<ReservationResponse>> response =
                reservationController.getMyReservations(
                        authentication,
                        filter
                );

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertSame(
                expectedPage,
                response.getBody()
        );
    }

    @Test
    void getAllReservationsShouldReturnOk() {

        ReservationFilterRequest filter =
                new ReservationFilterRequest();

        filter.setStatus(ReservationStatus.PENDING);
        filter.setMinPrice(new BigDecimal("100"));
        filter.setMaxPrice(new BigDecimal("1000"));
        filter.setPage(0);
        filter.setSize(5);
        filter.setSortBy("price");
        filter.setSortDirection("asc");

        Page<ReservationResponse> expectedPage =
                mock(Page.class);

        when(reservationService.getAllReservations(
                ReservationStatus.PENDING,
                new BigDecimal("100"),
                new BigDecimal("1000"),
                0,
                5,
                "price",
                "asc"
        )).thenReturn(expectedPage);

        ResponseEntity<Page<ReservationResponse>> response =
                reservationController.getAllReservations(
                        filter
                );

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertSame(
                expectedPage,
                response.getBody()
        );
    }
    @Test
    void getReservationByIdShouldReturnOkForAdmin() {

        Authentication authentication =
                new org.springframework.security.authentication
                        .UsernamePasswordAuthenticationToken(
                                "admin",
                                null,
                                java.util.List.of(
                                        new org.springframework.security.core.authority
                                                .SimpleGrantedAuthority("ROLE_ADMIN")
                                )
                        );

        ReservationResponse expectedResponse =
                mock(ReservationResponse.class);

        when(reservationService.getReservationById(
                1L,
                "admin",
                true
        )).thenReturn(expectedResponse);

        ResponseEntity<ReservationResponse> response =
                reservationController.getReservationById(
                        1L,
                        authentication
                );

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertSame(
                expectedResponse,
                response.getBody()
        );
    }

    @Test
    void updateReservationShouldReturnOk() {

        ReservationRequest request =
                mock(ReservationRequest.class);

        ReservationResponse expectedResponse =
                mock(ReservationResponse.class);

        when(reservationService.updateReservation(
                1L,
                request
        )).thenReturn(expectedResponse);

        ResponseEntity<ReservationResponse> response =
                reservationController.updateReservation(
                        1L,
                        request
                );

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertSame(
                expectedResponse,
                response.getBody()
        );
    }

    @Test
    void deleteReservationShouldReturnNoContent() {

        doNothing()
                .when(reservationService)
                .deleteReservation(1L);

        ResponseEntity<Void> response =
                reservationController.deleteReservation(1L);

        assertEquals(
                HttpStatus.NO_CONTENT,
                response.getStatusCode()
        );

        verify(reservationService)
                .deleteReservation(1L);
    }
}