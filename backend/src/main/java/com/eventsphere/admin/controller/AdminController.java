package com.eventsphere.admin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: admin-only endpoints, guarded with @PreAuthorize("hasRole('ADMIN')").
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {
	
	@GetMapping("/test")
	public String testing () {
		return "Working";
	}
}
