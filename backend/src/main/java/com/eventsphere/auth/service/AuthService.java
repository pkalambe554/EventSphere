package com.eventsphere.auth.service;

import java.security.DrbgParameters.Reseed;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eventsphere.auth.dto.AuthResponse;
import com.eventsphere.auth.dto.LoginRequest;
import com.eventsphere.auth.dto.RegisterRequest;
import com.eventsphere.auth.entity.PasswordResetToken;
import com.eventsphere.auth.entity.Role;
import com.eventsphere.auth.entity.User;
import com.eventsphere.auth.repository.PasswordResetTokenRepository;
import com.eventsphere.auth.repository.UserRepository;
import com.eventsphere.auth.security.JwtUtil;
import com.eventsphere.common.exception.ApiException;
import com.eventsphere.notification.EmailService;

/**
 * TODO: register(RegisterRequest) and login(LoginRequest) -> AuthResponse.
 * Hash passwords with PasswordEncoder, issue tokens with JwtUtil.
 */
@Service
public class AuthService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final PasswordResetTokenRepository passwordResetTokenRepository;
	private final EmailService emailService;
	
	public AuthService ( UserRepository userRepository ,
			PasswordEncoder passwordEncoder,
			JwtUtil jwtUtil,
			PasswordResetTokenRepository passwordResetTokenRepository,
			EmailService emailService
			) {
		this.userRepository=userRepository;
		this.passwordEncoder=passwordEncoder;
		this.jwtUtil=jwtUtil;	
		this.passwordResetTokenRepository=passwordResetTokenRepository;
		this.emailService=emailService; 
	}
	
	public AuthResponse register (RegisterRequest request) {
		if(userRepository.existsByEmail(request.getEmail())) {
			System.out.println("exis-->"+request.getEmail());
		 throw	new ApiException(HttpStatus.CONFLICT, "An User Account With This Email Is already exists.");
		}
		User user= new User(); 
		user.setEmail(request.getEmail());
		user.setRole(Role.USER);
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		System.out.println("user-->"+user);
		userRepository.save(user);
		
		String token = jwtUtil.getAccessToken(request.getEmail(), user.getRole().name());
		
		AuthResponse auth = new AuthResponse();
		auth.setAccessToken(token);
		auth.setEmail(user.getEmail());
		auth.setRole(user.getRole().name());
		return auth;
	}
	
	
	public AuthResponse login (LoginRequest login) {
		User user = userRepository.findByEmail(login.getEmail())
				.orElseThrow(()->  new ApiException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));
		if(!passwordEncoder.matches(login.getPassword(),user.getPassword())) {
		throw	new ApiException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
		}
		String token =jwtUtil.getAccessToken(user.getEmail(), user.getRole().name());
		AuthResponse response = new AuthResponse();
		response.setAccessToken(token);
		response.setEmail(user.getEmail());
		response.setRole(user.getRole().name());
		
		return response;
	}
	
	public void forgotPassword(String email) {
	    Optional<User> userOpt = userRepository.findByEmail(email);
	    if (userOpt.isEmpty()) {
	        return; // silent - don't reveal whether the email exists
	    }

	    User user = userOpt.get();
	    String token = UUID.randomUUID().toString();

	    PasswordResetToken resetToken = new PasswordResetToken();
	    resetToken.setUser(user);
	    resetToken.setToken(token);
	    resetToken.setExpiresAt(Instant.now().plusSeconds(900));
	    resetToken.setUsed(false);
	    passwordResetTokenRepository.save(resetToken);

	    String resetLink = "http://localhost:4200/reset-password?token=" + token;
	    emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
	}

	public void resetPassword(String token, String newPassword) {
	    PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
	            .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Invalid or expired reset link"));

	    if (resetToken.isUsed() || resetToken.getExpiresAt().isBefore(Instant.now())) {
	        throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid or expired reset link");
	    }

	    User user = resetToken.getUser();
	    user.setPassword(passwordEncoder.encode(newPassword));
	    userRepository.save(user);

	    resetToken.setUsed(true);
	    passwordResetTokenRepository.save(resetToken);
	}
}

