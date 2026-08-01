package com.eventsphere.notification;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.eventsphere.booking.entity.BookingConfirmedEvent;

@Component
public class NotificationProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public NotificationProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishBookingConfirmed(Long bookingId, String userEmail) {
        kafkaTemplate.send("booking-events", new BookingConfirmedEvent(bookingId, userEmail));
    }
}