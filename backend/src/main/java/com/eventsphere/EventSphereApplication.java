package com.eventsphere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@EnableScheduling
@SpringBootApplication
public class EventSphereApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventSphereApplication.class, args);
    }
}
