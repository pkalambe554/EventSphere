package com.eventsphere.booking.service;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.eventsphere.booking.entity.SeatStatus;
import com.eventsphere.booking.entity.Seats;
import com.eventsphere.booking.repository.SeatRepository;
import com.eventsphere.common.exception.ApiException;

import jakarta.transaction.Transactional;

@Service
public class BookingService {
	private final SeatRepository seatRepository; 
	public BookingService(SeatRepository seatRepository) {
		this.seatRepository=seatRepository;
	}
	
	@Transactional
	public Seats holdSeat(Long seatId){
	Seats st=	this.seatRepository.findWithLockBySeatId(seatId)
				.orElseThrow(()-> new ApiException(HttpStatus.NOT_FOUND,"Seat is not found..."));
	if(!st.getSeatStatus().equals(SeatStatus.AVAILABLE)) {
		throw new ApiException(HttpStatus.CONFLICT, "Seat is no longer available");
	}	
	st.setSeatStatus(SeatStatus.LOCKED);
	return this.seatRepository.save(st);
		
	}
}
