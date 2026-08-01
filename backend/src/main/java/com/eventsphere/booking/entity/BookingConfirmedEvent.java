package com.eventsphere.booking.entity;

public class BookingConfirmedEvent {
    private Long bookingId;
    private String userEmail;

    public BookingConfirmedEvent() {
        // no-arg constructor - needed for Jackson to deserialize this on the consumer side later
    }

    public BookingConfirmedEvent(Long bookingId, String userEmail) {
        this.bookingId = bookingId;
        this.userEmail = userEmail;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}