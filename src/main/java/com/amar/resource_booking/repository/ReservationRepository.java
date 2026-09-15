package com.amar.resource_booking.repository;


import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.amar.resource_booking.entity.Reservation;
import com.amar.resource_booking.entity.ReservationStatus;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	
	
	  @Query("""
	            SELECT r FROM Reservation r
	            WHERE (:status IS NULL OR r.status = :status)
	            AND (:minPrice IS NULL OR r.price >= :minPrice)
	            AND (:maxPrice IS NULL OR r.price <= :maxPrice)
	            """)
	    Page<Reservation> findReservations(
	            @Param("status") ReservationStatus status,
	            @Param("minPrice") BigDecimal minPrice,
	            @Param("maxPrice") BigDecimal maxPrice,
	            Pageable pageable
	    );

	    @Query("""
	            SELECT r FROM Reservation r
	            WHERE r.user.id = :userId
	            AND (:status IS NULL OR r.status = :status)
	            AND (:minPrice IS NULL OR r.price >= :minPrice)
	            AND (:maxPrice IS NULL OR r.price <= :maxPrice)
	            """)
	    Page<Reservation> findMyReservations(
	            @Param("userId") Long userId,
	            @Param("status") ReservationStatus status,
	            @Param("minPrice") BigDecimal minPrice,
	            @Param("maxPrice") BigDecimal maxPrice,
	            Pageable pageable
	    );

}
