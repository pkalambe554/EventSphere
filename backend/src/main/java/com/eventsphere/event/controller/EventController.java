package com.eventsphere.event.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: GET /, GET /{id}, POST / (admin only later).
 */
@RestController
@RequestMapping("/api/events")
public class EventController {
	  @GetMapping
	    public String ping() {
	        return "Event module reachable";
	    }
}
