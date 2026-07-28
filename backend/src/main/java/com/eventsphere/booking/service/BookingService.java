package com.eventsphere.booking.service;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.eventsphere.auth.entity.User;
import com.eventsphere.auth.repository.UserRepository;
import com.eventsphere.booking.entity.Booking;
import com.eventsphere.booking.entity.SeatStatus;
import com.eventsphere.booking.entity.Seats;
import com.eventsphere.booking.entity.Status;
import com.eventsphere.booking.repository.BookingRepository;
import com.eventsphere.booking.repository.SeatRepository;
import com.eventsphere.common.exception.ApiException;

import jakarta.transaction.Transactional;

@Service
public class BookingService {
	private final SeatRepository seatRepository;
	private final UserRepository userRepository;
	private final BookingRepository bookingRepository;
	public BookingService(SeatRepository seatRepository,UserRepository userRepository,BookingRepository bookingRepository) {
		this.seatRepository=seatRepository;
		this.userRepository=userRepository;
		this.bookingRepository=bookingRepository;
	}
	
	@Transactional
	public Booking holdSeat(Long seatId){
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
	User user= this.userRepository.findByEmail(email).orElseThrow(()-> new ApiException(HttpStatus.NOT_FOUND,"user not found" ));
	Seats st=	this.seatRepository.findWithLockBySeatId(seatId)
				.orElseThrow(()-> new ApiException(HttpStatus.NOT_FOUND,"Seat is not found..."));
	if(!st.getSeatStatus().equals(SeatStatus.AVAILABLE)) {
		throw new ApiException(HttpStatus.CONFLICT, "Seat is no longer available");
	}	
	st.setSeatStatus(SeatStatus.LOCKED);
	this.seatRepository.save(st);
	
	Booking booking = new Booking();
	booking.setSeats(st);
	booking.setUser(user);
	booking.setStatus(Status.SEATS_LOCKED);
	booking.setExpiresAt(Instant.now().plusSeconds(600));
	return this.bookingRepository.save(booking);
	
	}
	
	public void releaseExpiredHolds() {
		System.out.print("calling scheduler...");
		List<Booking> bookings = this.bookingRepository.findByStatusAndExpiresAtBefore(Status.SEATS_LOCKED,Instant.now());
		for(Booking b : bookings) {
		Seats s = b.getSeats();
		s.setSeatStatus(SeatStatus.AVAILABLE);
		this.seatRepository.save(s);
		
		b.setStatus(Status.EXPIRED);
		this.bookingRepository.save(b);
		}
	}
	
}
