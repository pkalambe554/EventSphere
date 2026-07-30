package com.eventsphere.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventsphere.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long>{

	Optional<Payment>findByIdempotencyKey(String idempotencyKey);
}
