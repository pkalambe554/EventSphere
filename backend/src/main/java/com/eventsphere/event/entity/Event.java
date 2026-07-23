package com.eventsphere.event.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * TODO: id, title, description, venue, category, eventDateTime,
 * totalSeats, availableSeats, createdAt.
 */
@Entity
@Table(name = "events")
public class Event {
	@Id
	@GeneratedValue(strategy =GenerationType.AUTO)
	@Column(name="EVENT_ID")
	private Long event_id;
}
