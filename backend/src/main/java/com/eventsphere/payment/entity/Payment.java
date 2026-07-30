package com.eventsphere.payment.entity;

import java.math.BigDecimal;
import java.time.Instant;

import com.eventsphere.booking.entity.Booking;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name="PAYMENT")
public class Payment {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name="ID")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="BOOK_ID")
	private Booking booking;
	
	@Column(name="AMOUNT")
	private BigDecimal  amount;
	
	@Enumerated(EnumType.STRING)
	@Column(name="PAYMENT_STATUS")
	private PaymentStatus paymentStatus;
	
	@Column(name="PAYMENT_REFERENCE")
	private String paymentReference;
	
	@Column(name="IDEMPOTENCY_KEY" ,unique = true)
	private String idempotencyKey;
	
	@Column(name="CREATED_AT",nullable=false,updatable = false)
	private Instant createdAt;
	
	@PrePersist
	void onCreate() {
		createdAt = Instant.now();		 
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Booking getBooking() {
		return booking;
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getPaymentReference() {
		return paymentReference;
	}

	public void setPaymentReference(String paymentReference) {
		this.paymentReference = paymentReference;
	}

	public String getIdempotencyKey() {
		return idempotencyKey;
	}

	public void setIdempotencyKey(String idempotencyKey) {
		this.idempotencyKey = idempotencyKey;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
	
	

}
