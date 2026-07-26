package com.eventsphere.event.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.eventsphere.common.exception.ApiException;
import com.eventsphere.event.entity.Event;
import com.eventsphere.event.repository.EventRepository;

/**
 * TODO: getAll(), getById(id), create(event).
 * Milestone 2: wrap reads with Redis cache-aside (@Cacheable + evict on write).
 */
@Service
public class EventService {
	private final EventRepository  eventRepository;
	public EventService (EventRepository eventRepository) {
		this.eventRepository=eventRepository;
	}
		
	public List<Event> getAllEvent (){
		return this.eventRepository.findAll();
	}
	
	public Event getById(Long id) {
		return eventRepository.findById(id)
				.orElseThrow(()-> new ApiException(HttpStatus.NOT_FOUND, "Event " + id + " not found"));
		 
	}
	
	public  Event create(Event ev){
		return this.eventRepository.save(ev);
	}
}
