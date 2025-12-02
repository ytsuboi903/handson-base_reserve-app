package com.booking.controller;

import com.booking.model.Booking;
import com.booking.model.BookingStatus;
import com.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Booking management
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class BookingController {

    private final BookingService bookingService;

    /**
     * Get all bookings
     * 
     * @return list of all bookings
     */
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings(
            @RequestParam(required = false) Long resourceId,
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(required = false) String customerEmail,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        List<Booking> bookings;
        
        if (customerEmail != null) {
            bookings = bookingService.getBookingsByCustomerEmail(customerEmail);
        } else if (resourceId != null) {
            bookings = bookingService.getBookingsByResourceId(resourceId);
        } else if (status != null) {
            bookings = bookingService.getBookingsByStatus(status);
        } else if (start != null && end != null) {
            bookings = bookingService.getBookingsByTimeRange(start, end);
        } else {
            bookings = bookingService.getAllBookings();
        }
        
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get booking by ID
     * 
     * @param id booking ID
     * @return booking details
     */
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Check resource availability
     * 
     * @param resourceId resource ID
     * @param start start time
     * @param end end time
     * @return availability status
     */
    @GetMapping("/available")
    public ResponseEntity<Map<String, Boolean>> checkAvailability(
            @RequestParam Long resourceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        boolean available = bookingService.isResourceAvailable(resourceId, start, end);
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", available);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Create a new booking
     * 
     * @param booking booking to create
     * @return created booking
     */
    @PostMapping
    public ResponseEntity<?> createBooking(@Valid @RequestBody Booking booking) {
        try {
            Booking created = bookingService.createBooking(booking);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (BookingService.BookingConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Update an existing booking
     * 
     * @param id booking ID
     * @param booking updated booking details
     * @return updated booking
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBooking(
            @PathVariable Long id,
            @Valid @RequestBody Booking booking) {
        try {
            Booking updated = bookingService.updateBooking(id, booking);
            return ResponseEntity.ok(updated);
        } catch (BookingService.BookingNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (BookingService.BookingConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Cancel a booking
     * 
     * @param id booking ID
     * @return cancelled booking
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        try {
            Booking cancelled = bookingService.cancelBooking(id);
            return ResponseEntity.ok(cancelled);
        } catch (BookingService.BookingNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a booking
     * 
     * @param id booking ID
     * @return no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        try {
            bookingService.deleteBooking(id);
            return ResponseEntity.noContent().build();
        } catch (BookingService.BookingNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Helper method to create error response
     */
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}

