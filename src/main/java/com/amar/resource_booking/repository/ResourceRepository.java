package com.amar.resource_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amar.resource_booking.entity.Resource;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
	
	 @Override
	    @Lock(LockModeType.PESSIMISTIC_WRITE)
	    java.util.Optional<Resource> findById(Long id);
	
	

}
