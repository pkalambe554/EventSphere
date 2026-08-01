package com.eventsphere.notification;

import com.eventsphere.booking.entity.BookingConfirmedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    @KafkaListener(topics = "booking-events", groupId = "notification-group")
    public void handleBookingConfirmed(BookingConfirmedEvent event) {
        System.out.println("Sending confirmation email to " + event.getUserEmail() + " for booking " + event.getBookingId());
    }
}