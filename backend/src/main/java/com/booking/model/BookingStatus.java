package com.booking.model;

/**
 * Status enumeration for bookings
 */
public enum BookingStatus {
    /**
     * Booking is pending confirmation
     */
    PENDING,
    
    /**
     * Booking has been confirmed
     */
    CONFIRMED,
    
    /**
     * Booking has been cancelled
     */
    CANCELLED
}

