package com.amar.resource_booking.service;

import org.springframework.stereotype.Service;


import com.amar.resource_booking.dto.LoginRequest;
import com.amar.resource_booking.dto.LoginResponse;
import com.amar.resource_booking.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;


@Service
public class AuthService {
	
	 private final AuthenticationManager authenticationManager;
	    private final JwtService jwtService;

	    public AuthService(AuthenticationManager authenticationManager,
	                       JwtService jwtService) {
	        this.authenticationManager = authenticationManager;
	        this.jwtService = jwtService;
	    }

	    public LoginResponse login(LoginRequest request) {

	        authenticationManager.authenticate(
	                new UsernamePasswordAuthenticationToken(
	                        request.getUsername(),
	                        request.getPassword()
	                )
	        );

	        String token = jwtService.generateToken(
	                request.getUsername()
	        );

	        return new LoginResponse(token);
	    }

}
