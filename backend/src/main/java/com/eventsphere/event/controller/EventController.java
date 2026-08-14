package com.eventsphere.event.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventsphere.booking.entity.Seats;
import com.eventsphere.event.entity.Event;
import com.eventsphere.event.service.EventService;

import jakarta.validation.Valid;


/**
 * TODO: GET /, GET /{id}, POST / (admin only later).
 */
@RestController
@RequestMapping("/api/events")
public class EventController {
	private final EventService eventService;
	
	public EventController (EventService eventService) {
		this.eventService=eventService;
	}
	@GetMapping
	public List<Event> getAll(){
		return this.eventService.getAllEvent();
	} 
	
	@GetMapping("/{id}")
	public Event getById(@PathVariable Long id) {
		return this.eventService.getById(id);
	}
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/event")
	public ResponseEntity<Event> create (@Valid @RequestBody Event event){
		System.out.println("Events"+event.toString());
		Event ev= this.eventService.create(event);
		return ResponseEntity.status(HttpStatus.CREATED).body(ev);
	}
	
	@GetMapping("/{id}/available-seats")
	public long getAvailableSeats(@PathVariable Long id) {
	    Event event = eventService.getById(id);
	    return eventService.getAvailableSeatCount(event);
	}
	@GetMapping("/{id}/seats")
	public List<Seats> getSeats(@PathVariable Long id) {
	    Event event = eventService.getById(id);
	    return eventService.getSeatsForEvent(event);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{id}")
	public Event update(@PathVariable Long id, @Valid @RequestBody Event event) {
	    return eventService.update(id, event);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
	    eventService.delete(id);
	    return ResponseEntity.noContent().build();
	}
}
