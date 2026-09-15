package com.amar.resource_booking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amar.resource_booking.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	 Optional<User> findByUsername(String username);

}
