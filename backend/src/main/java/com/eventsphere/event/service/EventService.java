package com.eventsphere.event.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.eventsphere.booking.entity.SeatStatus;
import com.eventsphere.booking.entity.Seats;
import com.eventsphere.booking.repository.SeatRepository;
import com.eventsphere.common.exception.ApiException;
import com.eventsphere.event.entity.Event;
import com.eventsphere.event.repository.EventRepository;

import jakarta.validation.Valid;

/**
 * TODO: getAll(), getById(id), create(event).
 * Milestone 2: wrap reads with Redis cache-aside (@Cacheable + evict on write).
 */
@Service
public class EventService {
	private final EventRepository  eventRepository;
	private final SeatRepository seatRepository;

	public EventService (EventRepository eventRepository,SeatRepository seatRepository) {
		this.eventRepository=eventRepository;
		this.seatRepository=seatRepository;
	}
		
	public List<Event> getAllEvent (){
		return this.eventRepository.findAll();
	}
	
	public Event getById(Long id) {
		return eventRepository.findById(id)
				.orElseThrow(()-> new ApiException(HttpStatus.NOT_FOUND, "Event " + id + " not found"));
		 
	}
	
//	public  Event create(Event ev){
//		return this.eventRepository.save(ev);
//	}
	
	public long getAvailableSeatCount(Event event) {
	    return seatRepository.countByEventAndSeatStatus(event, SeatStatus.AVAILABLE);
	}

	public List<Seats> getSeatsForEvent(Event event) {
		// TODO Auto-generated method stub
		return seatRepository.findByEvent(event);
	}
	
	public Event create(Event event) {
	    Event savedEvent = eventRepository.save(event);

	    int seatsPerRow = 20;
	    int totalSeats = event.getTotalSeats();

	    for (int i = 0; i < totalSeats; i++) {
	        int rowIndex = i / seatsPerRow;        // 0 for first 20 seats, 1 for next 20, etc.
	        int seatNumberInRow = (i % seatsPerRow) + 1;  // cycles 1 through 20 within each row

	        char rowLetter = (char) ('A' + rowIndex);
	        String seatLabel = rowLetter + String.valueOf(seatNumberInRow);

	        Seats seat = new Seats();
	        seat.setEvent(savedEvent);
	        seat.setSeatNumber(seatLabel);
	        seat.setSeatStatus(SeatStatus.AVAILABLE);
	        seatRepository.save(seat);
	    }

	    return savedEvent;
	}

	public Event update(Long id, Event updatedEvent) {
	    Event existing = getById(id);
	    existing.setTitle(updatedEvent.getTitle());
	    existing.setDescription(updatedEvent.getDescription());
	    existing.setVenue(updatedEvent.getVenue());
	    existing.setCategory(updatedEvent.getCategory());
	    existing.setEventDateTime(updatedEvent.getEventDateTime());
	    // deliberately NOT touching totalSeats here - changing seat count after
	    // seats already exist is a bigger feature (add/remove seat rows), out of scope for now
	    return eventRepository.save(existing);
	}

	public void delete(Long id) {
	    Event event = getById(id);
	    seatRepository.deleteAll(seatRepository.findByEvent(event)); // remove seats first (FK constraint)
	    eventRepository.delete(event);
	}
}
