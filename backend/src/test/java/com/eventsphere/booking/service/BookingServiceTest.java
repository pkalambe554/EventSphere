package com.eventsphere.booking.service;

import com.eventsphere.booking.entity.SeatStatus;
import com.eventsphere.booking.entity.Seats;
import com.eventsphere.booking.repository.SeatRepository;
import com.eventsphere.common.exception.ApiException;
import com.eventsphere.event.entity.Event;
import com.eventsphere.event.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Proves the pessimistic lock actually prevents two concurrent requests
 * from both successfully holding the same seat. Deliberately NOT
 * @Transactional at the class/method level — see explanation below.
 */
@SpringBootTest
public class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private EventRepository eventRepository;

    private Long seatId;

    @BeforeEach
    void setUp() {
        // Create a real Event first — Seats requires a valid event_id foreign key.
        Event event = new Event();
        event.setTitle("Concurrency Test Event");
        event.setVenue("Test Venue");
        event.setCategory("Test");
        event.setEventDateTime(LocalDateTime.now().plusDays(1));
        event.setTotalSeats(1);
        Event savedEvent = eventRepository.save(event);

        // Create one AVAILABLE seat under that event.
        Seats seat = new Seats();
        seat.setEvent(savedEvent);
        seat.setSeatNumber("A1");
        seat.setSeatStatus(SeatStatus.AVAILABLE);
        Seats savedSeat = seatRepository.save(seat);

        this.seatId = savedSeat.getSeatId();
    }

    @Test
    void onlyOneThreadShouldSuccessfullyHoldTheSameSeat() throws InterruptedException {
        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger conflictCount = new AtomicInteger(0);

        Runnable holdAttempt = () -> {
            try {
                startLatch.await(); // block here until released, forcing simultaneous start
                bookingService.holdSeat(seatId);
                successCount.incrementAndGet();
            } catch (ApiException ex) {
                if (ex.getStatus() == HttpStatus.CONFLICT) {
                    conflictCount.incrementAndGet();
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        };

        executor.submit(holdAttempt);
        executor.submit(holdAttempt);

        startLatch.countDown(); // release both threads at (as close to) the same instant

        executor.shutdown();
        boolean finished = executor.awaitTermination(5, TimeUnit.SECONDS);

        assertEquals(true, finished, "Threads did not finish in time");
        assertEquals(1, successCount.get(), "Exactly one thread should succeed in holding the seat");
        assertEquals(1, conflictCount.get(), "Exactly one thread should get a CONFLICT for the already-locked seat");
    }
}