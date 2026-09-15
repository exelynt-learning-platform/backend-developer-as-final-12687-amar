package com.amar.resource_booking.controller;

import com.amar.resource_booking.dto.ReservationRequest;
import com.amar.resource_booking.dto.ReservationResponse;
import com.amar.resource_booking.entity.ReservationStatus;
import com.amar.resource_booking.service.ReservationService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;


@RestController
@RequestMapping("/api/reservations")
@Validated
public class ReservationController {

	private final ReservationService reservationService;

	public ReservationController(ReservationService reservationService) {
		this.reservationService = reservationService;
	}

	@PostMapping
	public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request,
			Authentication authentication) {

		String username = authentication.getName();

		ReservationResponse response = reservationService.createReservation(request, username);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/my")
	public ResponseEntity<Page<ReservationResponse>> getMyReservations(

			Authentication authentication,

			@RequestParam(required = false) ReservationStatus status,

			@RequestParam(required = false) BigDecimal minPrice,

			@RequestParam(required = false) BigDecimal maxPrice,

			@RequestParam(defaultValue = "0")
			@Min(value = 0, message = "Page cannot be negative")
			int page,

			@RequestParam(defaultValue = "5")
			@Min(value = 1, message = "Size must be greater than 0")
			int size,
			@RequestParam(defaultValue = "id") String sortBy,

			@RequestParam(defaultValue = "asc") String sortDirection) {

		return ResponseEntity.ok(reservationService.getMyReservations(authentication.getName(), status, minPrice,
				maxPrice, page, size, sortBy, sortDirection));
	}

	
	@GetMapping
	public ResponseEntity<Page<ReservationResponse>> getAllReservations(

			@RequestParam(required = false) ReservationStatus status,

			@RequestParam(required = false) BigDecimal minPrice,

			@RequestParam(required = false) BigDecimal maxPrice,

			@RequestParam(defaultValue = "0") int page,

			@RequestParam(defaultValue = "5") int size,

			@RequestParam(defaultValue = "id") String sortBy,

			@RequestParam(defaultValue = "asc") String sortDirection) {

		return ResponseEntity.ok(
				reservationService.getAllReservations(status, minPrice, maxPrice, page, size, sortBy, sortDirection));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ReservationResponse> getReservationById(@PathVariable Long id,
			Authentication authentication) {

		String username = authentication.getName();

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

		return ResponseEntity.ok(reservationService.getReservationById(id, username, isAdmin));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ReservationResponse> updateReservation(@PathVariable Long id,
			@Valid @RequestBody ReservationRequest request) {

		return ResponseEntity.ok(reservationService.updateReservation(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {

		reservationService.deleteReservation(id);

		return ResponseEntity.noContent().build();
	}

}
