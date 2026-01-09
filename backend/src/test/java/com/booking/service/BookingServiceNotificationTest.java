package com.booking.service;

import com.booking.model.Booking;
import com.booking.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BookingServiceNotificationTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private NotificationService notificationService;

    private BookingService bookingService;

    private Booking newBooking;

    @BeforeEach
    void setUp() {
        newBooking = new Booking();
        newBooking.setId(null);
        newBooking.setResourceId(1L);
        newBooking.setCustomerName("テスト");
        newBooking.setCustomerEmail("test@example.com");
        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        newBooking.setStartTime(start);
        newBooking.setEndTime(start.plusHours(2));
        bookingService = new BookingService(bookingRepository);
        bookingService.setNotificationService(notificationService);
    }

    @Test
    void should_createNotification_when_bookingCreated() {
        // Arrange
        Booking saved = new Booking();
        saved.setId(100L);
        saved.setResourceId(newBooking.getResourceId());
        saved.setStartTime(newBooking.getStartTime());
        saved.setEndTime(newBooking.getEndTime());

        when(bookingRepository.findConflictingBookings(eq(newBooking.getResourceId()), any(), any(), anyList()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.save(newBooking)).thenReturn(saved);

        // Act
        Booking result = bookingService.createBooking(newBooking);

        // Assert
        assertThat(result).isNotNull();
        verify(bookingRepository).save(newBooking);
        verify(notificationService).createNotificationForBooking(any(Booking.class));
    }
}
