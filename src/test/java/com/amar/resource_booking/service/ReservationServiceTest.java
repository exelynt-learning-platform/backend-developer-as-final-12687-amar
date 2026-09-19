package com.amar.resource_booking.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.amar.resource_booking.dto.ReservationRequest;
import com.amar.resource_booking.dto.ReservationResponse;
import com.amar.resource_booking.entity.Reservation;
import com.amar.resource_booking.entity.ReservationStatus;
import com.amar.resource_booking.entity.Resource;
import com.amar.resource_booking.entity.Role;
import com.amar.resource_booking.entity.User;
import com.amar.resource_booking.exception.BadRequestException;
import com.amar.resource_booking.exception.ForbiddenException;
import com.amar.resource_booking.exception.ResourceNotFoundException;
import com.amar.resource_booking.repository.ReservationRepository;
import com.amar.resource_booking.repository.ResourceRepository;
import com.amar.resource_booking.repository.UserRepository;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private ReservationService reservationService;

    private User user;
    private User anotherUser;
    private Resource resource;
    private Reservation reservation;

    @BeforeEach
    void setUp() {

        user = new User("user", "password", Role.USER);
        user.setId(1L);

        anotherUser = new User("another", "password", Role.USER);
        anotherUser.setId(2L);

        resource = new Resource(
                "Conference Room",
                "Meeting room",
                "ROOM",
                true,
                new BigDecimal("600.00")
        );
        resource.setId(1L);

        reservation = new Reservation();
        reservation.setUser(user);
        reservation.setResource(resource);
        reservation.setStartDate(
                LocalDateTime.of(2026, 9, 20, 10, 0)
        );
        reservation.setEndDate(
                LocalDateTime.of(2026, 9, 20, 12, 0)
        );
        reservation.setPrice(new BigDecimal("600.00"));
        reservation.setStatus(ReservationStatus.PENDING);
    }

    private ReservationRequest createRequest() {

        ReservationRequest request =
                new ReservationRequest();

        request.setResourceId(1L);
        request.setStartDate(
                LocalDateTime.of(2026, 9, 20, 10, 0)
        );
        request.setEndDate(
                LocalDateTime.of(2026, 9, 20, 12, 0)
        );

        return request;
    }

    @Test
    void createReservationShouldCreatePendingReservation() {

        ReservationRequest request = createRequest();

        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.of(user));

        when(resourceRepository.findByIdForUpdate(1L))
        .thenReturn(Optional.of(resource));

        when(reservationRepository
                .existsOverlappingReservation(
                        anyLong(),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class),
                        anyList()
                ))
                .thenReturn(false);

        when(reservationRepository.save(any(Reservation.class)))
                .thenReturn(reservation);

        ReservationResponse response =
                reservationService.createReservation(
                        request,
                        "user"
                );

        assertEquals(
                ReservationStatus.PENDING,
                response.getStatus()
        );

        assertEquals(
                new BigDecimal("600.00"),
                response.getPrice()
        );

        verify(reservationRepository)
                .save(any(Reservation.class));
    }

    @Test
    void createReservationShouldFailWhenResourceNotAvailable() {

        resource.setAvailable(false);

        ReservationRequest request = createRequest();

        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.of(user));

        when(resourceRepository.findByIdForUpdate(1L))
        .thenReturn(Optional.of(resource));
        assertThrows(
                BadRequestException.class,
                () -> reservationService.createReservation(
                        request,
                        "user"
                )
        );

        verify(reservationRepository, never())
                .save(any(Reservation.class));
    }

    @Test
    void createReservationShouldFailWhenDatesAreInvalid() {

        ReservationRequest request = createRequest();

        request.setStartDate(
                LocalDateTime.of(2026, 9, 20, 14, 0)
        );

        request.setEndDate(
                LocalDateTime.of(2026, 9, 20, 12, 0)
        );

        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.of(user));

        when(resourceRepository.findByIdForUpdate(1L))
        .thenReturn(Optional.of(resource));

        assertThrows(
                BadRequestException.class,
                () -> reservationService.createReservation(
                        request,
                        "user"
                )
        );
    }

    @Test
    void createReservationShouldFailWhenTimeIsAlreadyReserved() {

        ReservationRequest request = createRequest();

        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.of(user));

        when(resourceRepository.findByIdForUpdate(1L))
        .thenReturn(Optional.of(resource));
        when(reservationRepository
                .existsOverlappingReservation(
                        anyLong(),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class),
                        anyList()
                ))
                .thenReturn(true);

        assertThrows(
                BadRequestException.class,
                () -> reservationService.createReservation(
                        request,
                        "user"
                )
        );
    }

    @Test
    void getReservationByIdShouldAllowOwner() {

        reservation.setId(1L);

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation));

        ReservationResponse response =
                reservationService.getReservationById(
                        1L,
                        "user",
                        false
                );

        assertEquals(
                ReservationStatus.PENDING,
                response.getStatus()
        );
    }

    @Test
    void getReservationByIdShouldRejectAnotherUser() {

        reservation.setId(1L);

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation));

        assertThrows(
                ForbiddenException.class,
                () -> reservationService.getReservationById(
                        1L,
                        "another",
                        false
                )
        );
    }

    @Test
    void getReservationByIdShouldAllowAdmin() {

        reservation.setId(1L);

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation));

        ReservationResponse response =
                reservationService.getReservationById(
                        1L,
                        "admin",
                        true
                );

        assertEquals(ReservationStatus.PENDING,
                response.getStatus());
    }

    @Test
    void getReservationByIdShouldThrowWhenNotFound() {

        when(reservationRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> reservationService.getReservationById(
                        99L,
                        "user",
                        false
                )
        );
    }

    @Test
    void getMyReservationsShouldReturnUserReservations() {

        reservation.setId(1L);

        Page<Reservation> page =
                new PageImpl<>(List.of(reservation));

        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.of(user));

        when(reservationRepository.findMyReservations(
                eq(1L),
                isNull(),
                isNull(),
                isNull(),
                any()
        )).thenReturn(page);

        Page<ReservationResponse> result =
                reservationService.getMyReservations(
                        "user",
                        null,
                        null,
                        null,
                        0,
                        5,
                        "id",
                        "asc"
                );

        assertEquals(1, result.getTotalElements());
        assertEquals(
                "user",
                result.getContent()
                        .get(0)
                        .getUsername()
        );
    }

    @Test
    void getAllReservationsShouldReturnReservations() {

        reservation.setId(1L);

        Page<Reservation> page =
                new PageImpl<>(List.of(reservation));

        when(reservationRepository.findReservations(
                isNull(),
                isNull(),
                isNull(),
                any()
        )).thenReturn(page);

        Page<ReservationResponse> result =
                reservationService.getAllReservations(
                        null,
                        null,
                        null,
                        0,
                        5,
                        "id",
                        "asc"
                );

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updateReservationShouldUpdateStatus() {

        reservation.setId(1L);

        ReservationRequest request = createRequest();
        request.setStatus(ReservationStatus.CONFIRMED);

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation));

        when(resourceRepository.findById(1L))
                .thenReturn(Optional.of(resource));

        when(reservationRepository
                .existsOverlappingReservationForUpdate(
                        anyLong(),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class),
                        anyList(),
                        anyLong()
                ))
                .thenReturn(false);

        when(reservationRepository.save(any(Reservation.class)))
                .thenReturn(reservation);

        ReservationResponse response =
                reservationService.updateReservation(
                        1L,
                        request
                );

        assertEquals(
                ReservationStatus.CONFIRMED,
                response.getStatus()
        );

        verify(reservationRepository)
                .save(reservation);
    }

    @Test
    void deleteReservationShouldDeleteReservation() {

        reservation.setId(1L);

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation));

        reservationService.deleteReservation(1L);

        verify(reservationRepository)
                .delete(reservation);
    }
    
    @Test
    void cancelReservationShouldCancelReservation() {

        User user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setRole(Role.USER);

        Resource resource = new Resource();
        resource.setId(1L);
        resource.setName("Meeting Room");
        resource.setDescription("Test room");
        resource.setType("ROOM");
        resource.setAvailable(true);
        resource.setPrice(new BigDecimal("100.00"));

        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(user);
        reservation.setResource(resource);
        reservation.setStatus(ReservationStatus.PENDING);

        assertNotNull(reservation.getUser());
        assertNotNull(reservation.getResource());

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation));

        when(reservationRepository.save(reservation))
                .thenReturn(reservation);

        ReservationResponse response =
                reservationService.cancelReservation(
                        1L,
                        "user",
                        false
                );

        assertNotNull(response);

        assertEquals(
                ReservationStatus.CANCELLED,
                response.getStatus()
        );

        verify(reservationRepository)
                .findById(1L);

        verify(reservationRepository)
                .save(reservation);
    }
}