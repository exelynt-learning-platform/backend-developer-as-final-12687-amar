package com.amar.resource_booking.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.amar.resource_booking.dto.ReservationRequest;
import com.amar.resource_booking.dto.ReservationResponse;
import com.amar.resource_booking.entity.Reservation;
import com.amar.resource_booking.entity.ReservationStatus;
import com.amar.resource_booking.entity.Resource;
import com.amar.resource_booking.entity.User;
import com.amar.resource_booking.exception.BadRequestException;
import com.amar.resource_booking.exception.ForbiddenException;
import com.amar.resource_booking.exception.ResourceNotFoundException;
import com.amar.resource_booking.repository.ReservationRepository;
import com.amar.resource_booking.repository.ResourceRepository;
import com.amar.resource_booking.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import com.amar.resource_booking.exception.UserNotFoundException;

@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            UserRepository userRepository,
            ResourceRepository resourceRepository) {

        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.resourceRepository = resourceRepository;
    }

    public ReservationResponse createReservation(
            ReservationRequest request,
            String username) {

    	User user = userRepository.findByUsername(username)
    	        .orElseThrow(() ->
    	                new UserNotFoundException(
    	                        "User not found"
    	                ));

    	Resource resource = resourceRepository
    	        .findByIdForUpdate(request.getResourceId())
    	        .orElseThrow(() ->
    	                new ResourceNotFoundException("Resource not found"));
        if (!resource.isAvailable()) {
            throw new BadRequestException("Resource is not available");
        }

        validateDates(
                request.getStartDate(),
                request.getEndDate()
        );

      
        boolean overlapping =
                reservationRepository.existsOverlappingReservation(
                        resource.getId(),
                        request.getStartDate(),
                        request.getEndDate(),
                        getActiveStatuses()
                );

        if (overlapping) {
            throw new BadRequestException(
                    "Resource is already reserved for the selected time"
            );
        }

        Reservation reservation = new Reservation();

        reservation.setUser(user);
        reservation.setResource(resource);
        reservation.setStartDate(request.getStartDate());
        reservation.setEndDate(request.getEndDate());
        reservation.setPrice(resource.getPrice());
        reservation.setStatus(ReservationStatus.PENDING);

        Reservation savedReservation =
                reservationRepository.save(reservation);

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

        
        validatePriceRange(minPrice, maxPrice);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));
       

        Sort sort = createSort(sortBy, sortDirection);
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
    	validatePriceRange(minPrice, maxPrice);
       
        Sort sort = createSort(sortBy, sortDirection);

      

   
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
                                "Reservation not found with id: " + id
                        ));

        if (!isAdmin &&
                !reservation.getUser().getUsername().equals(username)) {

            throw new ForbiddenException(
                    "You can only view your own reservations"
            );
        }

        return mapToResponse(reservation);
    }

    public ReservationResponse updateReservation(
            Long id,
            ReservationRequest request) {

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Reservation not found with id: " + id
                        ));

        // ADMIN can cancel a reservation even if the resource is unavailable
        
        Resource resource = resourceRepository.findById(
                request.getResourceId()
        ).orElseThrow(() ->
                new ResourceNotFoundException("Resource not found"));

        if (!resource.isAvailable()) {
            throw new BadRequestException(
                    "Resource is not available"
            );
        }

        validateDates(
                request.getStartDate(),
                request.getEndDate()
        );

        boolean overlapping =
                reservationRepository
                        .existsOverlappingReservationForUpdate(
                                resource.getId(),
                                request.getStartDate(),
                                request.getEndDate(),
                                getActiveStatuses(),
                                id
                        );

        if (overlapping) {
            throw new BadRequestException(
                    "Resource is already reserved for the selected time"
            );
        }

        reservation.setResource(resource);
        reservation.setStartDate(request.getStartDate());
        reservation.setEndDate(request.getEndDate());
        reservation.setPrice(resource.getPrice());

        if (request.getStatus() != null) {
            reservation.setStatus(request.getStatus());
        }

        Reservation updatedReservation =
                reservationRepository.save(reservation);

        return mapToResponse(updatedReservation);
    }
    public void deleteReservation(Long id) {

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Reservation not found with id: " + id
                        ));

        reservationRepository.delete(reservation);
    }

    private Sort createSort(
            String sortBy,
            String sortDirection) {

        List<String> allowedFields = List.of(
                "id",
                "startDate",
                "endDate",
                "price",
                "status"
        );

        if (!allowedFields.contains(sortBy)) {
            throw new BadRequestException(
                    "Invalid sort field"
            );
        }

        Sort sort = Sort.by(sortBy);

        if ("desc".equalsIgnoreCase(sortDirection)) {
            return sort.descending();
        }

        if ("asc".equalsIgnoreCase(sortDirection)) {
            return sort.ascending();
        }

        throw new BadRequestException(
                "Sort direction must be asc or desc"
        );
    }

    private void validateDates(
            LocalDateTime startDate,
            LocalDateTime endDate) {

        if (!startDate.isBefore(endDate)) {
            throw new BadRequestException(
                    "Start date must be before end date"
            );
        }
    }
    private List<ReservationStatus> getActiveStatuses() {
        return List.of(
                ReservationStatus.PENDING,
                ReservationStatus.CONFIRMED
        );
    }
    private ReservationResponse mapToResponse(
            Reservation reservation) {

        return new ReservationResponse(
                reservation.getId(),
                reservation.getUser().getId(),
                reservation.getUser().getUsername(),
                reservation.getResource().getId(),
                reservation.getResource().getName(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getPrice(),
                reservation.getStatus()
        );
    }
    
    public ReservationResponse cancelReservation(
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
                    "You are not allowed to cancel this reservation");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);

        Reservation cancelledReservation =
                reservationRepository.save(reservation);

        return mapToResponse(cancelledReservation);
    }
    
    private void validatePriceRange(
            BigDecimal minPrice,
            BigDecimal maxPrice) {

        if (minPrice != null
                && maxPrice != null
                && minPrice.compareTo(maxPrice) > 0) {

            throw new BadRequestException(
                    "minPrice cannot be greater than maxPrice"
            );
        }
    }
}