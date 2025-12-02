package com.booking.service;

import com.booking.model.Booking;
import com.booking.model.BookingStatus;
import com.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for Booking management
 */
@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;

    /**
     * Get all bookings
     * 
     * @return list of all bookings
     */
    @Transactional(readOnly = true)
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    /**
     * Get booking by ID
     * 
     * @param id booking ID
     * @return Optional containing the booking if found
     */
    @Transactional(readOnly = true)
    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    /**
     * Get bookings by resource ID
     * 
     * @param resourceId resource ID
     * @return list of bookings for the specified resource
     */
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByResourceId(Long resourceId) {
        return bookingRepository.findByResourceId(resourceId);
    }

    /**
     * Get bookings by status
     * 
     * @param status booking status
     * @return list of bookings with specified status
     */
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByStatus(BookingStatus status) {
        return bookingRepository.findByStatus(status);
    }

    /**
     * Get bookings by customer email
     * 
     * @param email customer email
     * @return list of bookings for the specified customer
     */
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByCustomerEmail(String email) {
        return bookingRepository.findByCustomerEmail(email);
    }

    /**
     * Get bookings within a time range
     * 
     * @param start start time
     * @param end end time
     * @return list of bookings within the time range
     */
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByTimeRange(LocalDateTime start, LocalDateTime end) {
        return bookingRepository.findByTimeRange(start, end);
    }

    /**
     * Check if a resource is available for booking
     * 
     * @param resourceId resource ID
     * @param start start time
     * @param end end time
     * @return true if available, false if conflicting bookings exist
     */
    @Transactional(readOnly = true)
    public boolean isResourceAvailable(Long resourceId, LocalDateTime start, LocalDateTime end) {
        List<BookingStatus> activeStatuses = Arrays.asList(BookingStatus.PENDING, BookingStatus.CONFIRMED);
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                resourceId, start, end, activeStatuses);
        return conflicts.isEmpty();
    }

    /**
     * Create a new booking
     * 
     * @param booking booking to create
     * @return created booking
     * @throws BookingConflictException if resource is not available
     */
    public Booking createBooking(Booking booking) {
        if (booking.getId() != null) {
            throw new IllegalArgumentException("New booking should not have an ID");
        }

        // Validate time range
        if (!booking.getEndTime().isAfter(booking.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        // Check for conflicts
        if (!isResourceAvailable(booking.getResourceId(), booking.getStartTime(), booking.getEndTime())) {
            throw new BookingConflictException(
                "Resource is not available for the specified time range");
        }

        Booking created = bookingRepository.save(booking);

        return created;
    }

    /**
     * Update an existing booking
     * 
     * @param id booking ID
     * @param bookingDetails updated booking details
     * @return updated booking
     * @throws BookingNotFoundException if booking is not found
     * @throws BookingConflictException if resource is not available
     */
    public Booking updateBooking(Long id, Booking bookingDetails) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));

        // If time or resource is changing, check for conflicts
        boolean timeChanged = !booking.getStartTime().equals(bookingDetails.getStartTime()) ||
                             !booking.getEndTime().equals(bookingDetails.getEndTime());
        boolean resourceChanged = !booking.getResourceId().equals(bookingDetails.getResourceId());

        if (timeChanged || resourceChanged) {
            // Temporarily remove this booking to check availability
            List<BookingStatus> activeStatuses = Arrays.asList(BookingStatus.PENDING, BookingStatus.CONFIRMED);
            List<Booking> conflicts = bookingRepository.findConflictingBookings(
                    bookingDetails.getResourceId(), 
                    bookingDetails.getStartTime(), 
                    bookingDetails.getEndTime(), 
                    activeStatuses);
            
            // Remove self from conflicts
            conflicts.removeIf(b -> b.getId().equals(id));
            
            if (!conflicts.isEmpty()) {
                throw new BookingConflictException(
                    "Resource is not available for the specified time range");
            }
        }

        booking.setResourceId(bookingDetails.getResourceId());
        booking.setCustomerName(bookingDetails.getCustomerName());
        booking.setCustomerEmail(bookingDetails.getCustomerEmail());
        booking.setStartTime(bookingDetails.getStartTime());
        booking.setEndTime(bookingDetails.getEndTime());
        booking.setStatus(bookingDetails.getStatus());
        booking.setNotes(bookingDetails.getNotes());

        return bookingRepository.save(booking);
    }

    /**
     * Cancel a booking
     * 
     * @param id booking ID
     * @return cancelled booking
     * @throws BookingNotFoundException if booking is not found
     */
    public Booking cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));

        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    /**
     * Delete a booking
     * 
     * @param id booking ID
     * @throws BookingNotFoundException if booking is not found
     */
    public void deleteBooking(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new BookingNotFoundException("Booking not found with id: " + id);
        }
        bookingRepository.deleteById(id);
    }

    /**
     * Custom exception for booking not found
     */
    public static class BookingNotFoundException extends RuntimeException {
        public BookingNotFoundException(String message) {
            super(message);
        }
    }

    /**
     * Custom exception for booking conflicts
     */
    public static class BookingConflictException extends RuntimeException {
        public BookingConflictException(String message) {
            super(message);
        }
    }
}

