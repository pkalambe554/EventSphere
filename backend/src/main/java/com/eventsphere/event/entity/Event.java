package com.eventsphere.event.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * TODO: id, title, description, venue, category, eventDateTime,
 * totalSeats, availableSeats, createdAt.
 */
@Entity
@Table(name = "EVENT")
public class Event {
	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	@Column(name="EVENT_ID")
	private Long event_id;
	
	@Column(name="TITLE",nullable=false)
	private String title;
	
	@Column(name="DESCRIPTION",length = 2000)
	private String description;
	
	@Column(name="VENUE",nullable=false)
	private String venue;
	
	@Column(name="CATEGORY")
	private String category;
	
	@Column(name="EVENT_DATETIME",nullable=false)
	private LocalDateTime eventDateTime;
	
	@Column(name="TOTAL_SEATS",nullable=false)
	private Integer totalSeats;
	
	@Column(name="AVAILABLE_SEATS",nullable=false)
	private Integer availableSeats;
	
	@Column(name="CREATED_AT",nullable=false,updatable = false)
	private Instant createdAt;
	
	@PrePersist
	void onCreate() {
		createdAt = Instant.now();
	    System.out.println("totalSeats value: " + totalSeats);
		 if (availableSeats == null) {
		        availableSeats = totalSeats;
		    }
	}
	
	

	public Long getEvent_id() {
		return event_id;
	}



	public void setEvent_id(Long event_id) {
		this.event_id = event_id;
	}



	public String getTitle() {
		return title;
	}



	public void setTitle(String title) {
		this.title = title;
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	public String getVenue() {
		return venue;
	}



	public void setVenue(String venue) {
		this.venue = venue;
	}



	public String getCategory() {
		return category;
	}



	public void setCategory(String category) {
		this.category = category;
	}



	public LocalDateTime getEventDateTime() {
		return eventDateTime;
	}



	public void setEventDateTime(LocalDateTime eventDateTime) {
		this.eventDateTime = eventDateTime;
	}



	public Integer getTotalSeats() {
		return totalSeats;
	}



	public void setTotalSeats(Integer totalSeats) {
		this.totalSeats = totalSeats;
	}



	public Integer getAvailableSeats() {
		return availableSeats;
	}



	public void setAvailableSeats(Integer availableSeats) {
		this.availableSeats = availableSeats;
	}



	public Instant getCreatedAt() {
		return createdAt;
	}



	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}



	@Override
	public String toString() {
		return "Event [event_id=" + event_id + ", title=" + title + ", description=" + description + ", venue=" + venue
				+ ", category=" + category + ", eventDateTime=" + eventDateTime + ", totalSeats=" + totalSeats
				+ ", availableSeats=" + availableSeats + ", createdAt=" + createdAt + "]";
	}
	
	
	
}
