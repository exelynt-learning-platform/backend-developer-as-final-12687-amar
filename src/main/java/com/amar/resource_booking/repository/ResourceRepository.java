package com.amar.resource_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amar.resource_booking.entity.Resource;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
	
	

}
