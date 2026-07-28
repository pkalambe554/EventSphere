package com.eventsphere.booking.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventsphere.booking.entity.Booking;
import com.eventsphere.booking.entity.Seats;
import com.eventsphere.booking.service.BookingService;

/**
 * Milestone 3: Seat/Booking entities, pessimistic lock on seat row,
 * POST /hold, hold-expiry sweep job, concurrency test.
 */
@RestController
@RequestMapping("/api/bookings")
public class BookingController {
	private final BookingService bookingService;
	
	public BookingController(BookingService bookingService){
		this.bookingService=bookingService;
	}
	
	@PostMapping("/hold/{seatId}")
	public Booking holdSeat(@PathVariable Long seatId) {
		return this.bookingService.holdSeat(seatId);
	}
	
}
