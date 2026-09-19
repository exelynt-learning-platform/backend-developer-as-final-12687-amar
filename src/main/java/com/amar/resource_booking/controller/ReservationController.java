package com.amar.resource_booking.controller;



import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import com.amar.resource_booking.dto.ReservationRequest;
import com.amar.resource_booking.dto.ReservationResponse;

import com.amar.resource_booking.service.ReservationService;

import jakarta.validation.Valid;

import com.amar.resource_booking.dto.ReservationFilterRequest;

@RestController
@RequestMapping("/api/reservations")
@Validated
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request,
            Authentication authentication) {

        String username = authentication.getName();

        ReservationResponse response =
                reservationService.createReservation(
                        request,
                        username
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<Page<ReservationResponse>> getMyReservations(
            Authentication authentication,
            @Valid ReservationFilterRequest filter) {

        return ResponseEntity.ok(
                reservationService.getMyReservations(
                        authentication.getName(),
                        filter.getStatus(),
                        filter.getMinPrice(),
                        filter.getMaxPrice(),
                        filter.getPage(),
                        filter.getSize(),
                        filter.getSortBy(),
                        filter.getSortDirection()));
    }
    @GetMapping
    public ResponseEntity<Page<ReservationResponse>> getAllReservations(
            @Valid ReservationFilterRequest filter) {

        return ResponseEntity.ok(
                reservationService.getAllReservations(
                        filter.getStatus(),
                        filter.getMinPrice(),
                        filter.getMaxPrice(),
                        filter.getPage(),
                        filter.getSize(),
                        filter.getSortBy(),
                        filter.getSortDirection()));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservationById(
            @PathVariable Long id,
            Authentication authentication) {

        String username = authentication.getName();
        boolean isAdmin = isAdmin(authentication);

        return ResponseEntity.ok(
                reservationService.getReservationById(
                        id,
                        username,
                        isAdmin
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponse> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationRequest request) {

        return ResponseEntity.ok(
                reservationService.updateReservation(
                        id,
                        request
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(
            @PathVariable Long id) {

        reservationService.deleteReservation(id);

        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ReservationResponse> cancelReservation(
            @PathVariable Long id,
            Authentication authentication) {

        String username = authentication.getName();
        boolean isAdmin = isAdmin(authentication);

        return ResponseEntity.ok(
                reservationService.cancelReservation(
                        id,
                        username,
                        isAdmin
                )
        );
    }
    
    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));
    }
}