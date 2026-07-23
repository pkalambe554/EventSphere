/**
 * Notification module (Milestone 5).
 *
 * This module is consumer-driven, not HTTP-driven, so there is no controller
 * package here. When you build it out:
 *   - notification.consumer: a @KafkaListener (or RabbitMQ @RabbitListener)
 *     subscribed to a "booking-events" topic, deduped by bookingId so
 *     at-least-once delivery doesn't send duplicate emails.
 *   - notification.service: the actual send (email/SMS) logic, kept out of
 *     the booking request's critical path.
 *
 * Left empty for now so the app can start without requiring a live
 * Kafka broker.
 */
package com.eventsphere.notification;
