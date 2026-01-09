package com.booking.service;

import com.booking.model.Booking;
import com.booking.model.Notification;
import com.booking.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Booking testBooking;

    @BeforeEach
    void setUp() {
        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setResourceId(2L);
        testBooking.setCustomerName("テスト");
        testBooking.setCustomerEmail("test@example.com");
        testBooking.setStartTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0));
        testBooking.setEndTime(testBooking.getStartTime().plusHours(2));
    }

    @Test
    void should_createNotification_when_bookingCreated() {
        // Arrange
        Notification saved = new Notification();
        saved.setId(1L);
        saved.setBookingId(testBooking.getId());
        saved.setType("CREATED");
        saved.setTitle("予約作成完了");
        saved.setBody("dummy");

        when(notificationRepository.save(any(Notification.class))).thenReturn(saved);

        // Act
        Notification result = notificationService.createNotificationForBooking(testBooking);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("CREATED");
        assertThat(result.getBookingId()).isEqualTo(testBooking.getId());
        assertThat(result.getTitle()).isEqualTo("予約作成完了");
    }
}
