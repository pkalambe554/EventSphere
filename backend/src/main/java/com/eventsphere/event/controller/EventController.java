package com.eventsphere.event.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.eventsphere.event.entity.Event;
import com.eventsphere.event.service.EventService;


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
	
	@PostMapping("/event")
	public ResponseEntity<Event> create (@RequestBody Event event){
		System.out.println("Events"+event.toString());
		Event ev= this.eventService.create(event);
		return ResponseEntity.status(HttpStatus.CREATED).body(ev);
	}
	
	@GetMapping("/{id}/available-seats")
	public long getAvailableSeats(@PathVariable Long id) {
	    Event event = eventService.getById(id);
	    return eventService.getAvailableSeatCount(event);
	}
	
}
