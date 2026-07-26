package com.eventsphere.event.repository;

import com.eventsphere.event.entity.Event;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
    // TODO (Milestone 2): findByCategory, findByVenue, etc.
	 List<Event> findByCategory(String category);
	List<Event> findByVenue(String venue);

}
