package com.eventsphere.booking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.eventsphere.booking.entity.Seats;

import jakarta.persistence.LockModeType;

public interface SeatRepository extends JpaRepository<Seats, Long> {
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	 Optional<Seats>findWithLockBySeatId(Long seatId);

}
