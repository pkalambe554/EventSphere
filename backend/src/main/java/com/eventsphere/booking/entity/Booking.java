package com.eventsphere.booking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="BOOKING")
public class Booking {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="BOOK_ID")
	private Long bookId;
	
	
}
