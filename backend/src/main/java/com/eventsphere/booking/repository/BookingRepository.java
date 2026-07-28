package com.eventsphere.booking.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventsphere.booking.entity.Booking;
import com.eventsphere.booking.entity.Status;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	 public List<Booking> findByStatusAndExpiresAtBefore(Status status,Instant  now) ;
}
