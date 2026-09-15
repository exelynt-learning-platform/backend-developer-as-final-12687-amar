package com.amar.resource_booking.service;

import org.springframework.stereotype.Service;

import com.amar.resource_booking.dto.ReservationRequest;
import com.amar.resource_booking.dto.ReservationResponse;
import com.amar.resource_booking.entity.Reservation;
import com.amar.resource_booking.entity.ReservationStatus;
import com.amar.resource_booking.entity.Resource;
import com.amar.resource_booking.entity.User;
import com.amar.resource_booking.repository.ReservationRepository;
import com.amar.resource_booking.repository.ResourceRepository;
import com.amar.resource_booking.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.amar.resource_booking.exception.ForbiddenException;
import com.amar.resource_booking.exception.ResourceNotFoundException;

import java.math.BigDecimal;

@Service
public class ReservationService {

	private final ReservationRepository reservationRepository;
	private final UserRepository userRepository;
	private final ResourceRepository resourceRepository;

	public ReservationService(ReservationRepository reservationRepository, UserRepository userRepository,
			ResourceRepository resourceRepository) {

		this.reservationRepository = reservationRepository;
		this.userRepository = userRepository;
		this.resourceRepository = resourceRepository;
	}

	public ReservationResponse createReservation(ReservationRequest request, String username) {

		User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));

		Resource resource = resourceRepository.findById(request.getResourceId())
				.orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

		if (!resource.isAvailable()) {
			throw new RuntimeException("Resource is not available");
		}

		if (request.getStartDate().isAfter(request.getEndDate())) {
			throw new RuntimeException("Start date must be before end date");
		}

		Reservation reservation = new Reservation();

		reservation.setUser(user);
		reservation.setResource(resource);
		reservation.setStartDate(request.getStartDate());
		reservation.setEndDate(request.getEndDate());
		reservation.setPrice(resource.getPrice());
		reservation.setStatus(ReservationStatus.PENDING);

		Reservation savedReservation = reservationRepository.save(reservation);

		return mapToResponse(savedReservation);
	}

	public Page<ReservationResponse> getMyReservations(
	        String username,
	        ReservationStatus status,
	        BigDecimal minPrice,
	        BigDecimal maxPrice,
	        int page,
	        int size,
	        String sortBy,
	        String sortDirection) {

	    User user = userRepository.findByUsername(username)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException("User not found"));

	    Sort sort = Sort.by(sortBy);

	    if (sortDirection.equalsIgnoreCase("desc")) {
	        sort = sort.descending();
	    } else {
	        sort = sort.ascending();
	    }

	    Pageable pageable = PageRequest.of(page, size, sort);

	    return reservationRepository.findMyReservations(
	            user.getId(),
	            status,
	            minPrice,
	            maxPrice,
	            pageable
	    ).map(this::mapToResponse);
	}

	
	
	public Page<ReservationResponse> getAllReservations(
	        ReservationStatus status,
	        BigDecimal minPrice,
	        BigDecimal maxPrice,
	        int page,
	        int size,
	        String sortBy,
	        String sortDirection) {

	    Sort sort = Sort.by(sortBy);

	    if (sortDirection.equalsIgnoreCase("desc")) {
	        sort = sort.descending();
	    } else {
	        sort = sort.ascending();
	    }

	    Pageable pageable = PageRequest.of(page, size, sort);

	    return reservationRepository.findReservations(
	            status,
	            minPrice,
	            maxPrice,
	            pageable
	    ).map(this::mapToResponse);
	}
	

	public ReservationResponse getReservationById(
	        Long id,
	        String username,
	        boolean isAdmin) {

	    Reservation reservation = reservationRepository.findById(id)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException(
	                            "Reservation not found with id: " + id));

	    if (!isAdmin &&
	            !reservation.getUser().getUsername().equals(username)) {


	        throw new ForbiddenException(
	                "You can only view your own reservations");
	    }

	    return mapToResponse(reservation);
	}

	public ReservationResponse updateReservation(Long id, ReservationRequest request) {

		Reservation reservation = reservationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));

		Resource resource = resourceRepository.findById(request.getResourceId())
				.orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

		if (!resource.isAvailable()) {
			throw new RuntimeException("Resource is not available");
		}

		if (request.getStartDate().isAfter(request.getEndDate())) {
			throw new RuntimeException("Start date must be before end date");
		}

		reservation.setResource(resource);
		reservation.setStartDate(request.getStartDate());
		reservation.setEndDate(request.getEndDate());
		reservation.setPrice(resource.getPrice());
		
		if (request.getStatus() != null) {
		    reservation.setStatus(request.getStatus());
		}

		Reservation updatedReservation = reservationRepository.save(reservation);

		return mapToResponse(updatedReservation);
	}

	public void deleteReservation(Long id) {

		Reservation reservation = reservationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));

		reservationRepository.delete(reservation);
	}

	private ReservationResponse mapToResponse(Reservation reservation) {

		return new ReservationResponse(reservation.getId(), reservation.getUser().getId(),
				reservation.getUser().getUsername(), reservation.getResource().getId(),
				reservation.getResource().getName(), reservation.getStartDate(), reservation.getEndDate(),
				reservation.getPrice(), reservation.getStatus());
	}

}
