package com.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Booking Management System - Main Application
 * 
 * This is the entry point for the Spring Boot application.
 * It manages bookings for resources such as meeting rooms, facilities, etc.
 */
@SpringBootApplication
public class BookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookingApplication.class, args);
    }
}

