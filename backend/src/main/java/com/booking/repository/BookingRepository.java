package com.booking.repository;

import com.booking.model.Booking;
import com.booking.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Booking entity
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    /**
     * Find bookings by resource ID
     * 
     * @param resourceId ID of the resource
     * @return list of bookings for the specified resource
     */
    List<Booking> findByResourceId(Long resourceId);
    
    /**
     * Find bookings by status
     * 
     * @param status booking status
     * @return list of bookings with specified status
     */
    List<Booking> findByStatus(BookingStatus status);
    
    /**
     * Find bookings by customer email
     * 
     * @param email customer email address
     * @return list of bookings for the specified customer
     */
    List<Booking> findByCustomerEmail(String email);
    
    /**
     * Find bookings within a time range
     * 
     * @param start start of the time range
     * @param end end of the time range
     * @return list of bookings that overlap with the specified time range
     */
    @Query("SELECT b FROM Booking b WHERE b.startTime < :end AND b.endTime > :start")
    List<Booking> findByTimeRange(@Param("start") LocalDateTime start, 
                                   @Param("end") LocalDateTime end);
    
    /**
     * Find conflicting bookings for a resource within a time range
     * 
     * @param resourceId ID of the resource
     * @param start start of the time range
     * @param end end of the time range
     * @param status booking status (typically CONFIRMED or PENDING)
     * @return list of conflicting bookings
     */
    @Query("SELECT b FROM Booking b WHERE b.resourceId = :resourceId " +
           "AND b.startTime < :end AND b.endTime > :start " +
           "AND b.status IN :statuses")
    List<Booking> findConflictingBookings(@Param("resourceId") Long resourceId,
                                          @Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end,
                                          @Param("statuses") List<BookingStatus> statuses);
}

