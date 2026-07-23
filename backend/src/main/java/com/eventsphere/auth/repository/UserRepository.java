package com.eventsphere.auth.repository;

import com.eventsphere.auth.entity.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // TODO: findByEmail, existsByEmail
	Optional<User>findByEmail(String email);
	boolean existsByEmail(String email);
	
}
