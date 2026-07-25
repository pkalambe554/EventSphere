package com.eventsphere.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventsphere.auth.dto.AuthResponse;
import com.eventsphere.auth.dto.LoginRequest;
import com.eventsphere.auth.dto.RegisterRequest;
import com.eventsphere.auth.security.JwtUtil;
import com.eventsphere.auth.service.AuthService;

import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;

/**
 * TODO: POST /register, POST /login, delegating to AuthService.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	private JwtUtil util;
	

	private AuthService authService;
	
	public AuthController (AuthService authService) {
		this.authService=authService;
	}
	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
	AuthResponse auth=	authService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(auth);
	}
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
	AuthResponse auth=	authService.login(request);
		return ResponseEntity.ok(auth);
	}
	
	
	
	@GetMapping("/test/jwt")
	private String testjwt() {
		String token =util.getAccessToken("pkalmbe986@gmail.com", "ADMIN");
		   System.out.println("================================");
	        System.out.println("Generated Token:");
	        System.out.println(token);

	        // Parse Token
	        Claims claims = util.parseClaim(token);

	        System.out.println("\nParsed Claims");
	        System.out.println("Subject : " + claims.getSubject());
	        System.out.println("Role    : " + claims.get("role"));
	        System.out.println("Issued  : " + claims.getIssuedAt());
	        System.out.println("Expiry  : " + claims.getExpiration());

	        System.out.println("\nIs Valid : " + util.isValid(token));

	        System.out.println("================================");

	        return token;
	}
	
	
}
