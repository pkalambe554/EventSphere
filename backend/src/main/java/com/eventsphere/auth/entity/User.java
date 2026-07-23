package com.eventsphere.auth.entity;

import java.time.Instant;

import org.hibernate.validator.constraints.UniqueElements;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * TODO: add @Id, email, password (BCrypt hash), role, createdAt fields
 * plus Lombok getters/setters or a builder.
 */
@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="USER_ID")
	private Long user_id;
	
	
	@Column(name="EMAIL" , unique = true,nullable = false)
	private String email;
	
	@Column(name="PASSWORD")
	private String password;
	
	@Enumerated(EnumType.STRING)
	@Column(name="ROLE",nullable =false)
	private Role role;
	
	@Column(name="CREATED_AT",nullable=false,updatable = false)
	private Instant created_at;
	
	@PrePersist
	void onCreate() {
		created_at = Instant.now();
	}
 
	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Instant getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Instant created_at) {
		this.created_at = created_at;
	}
	
	
	}
