package com.booking.controller;

import com.booking.model.Notification;
import com.booking.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    @Test
    void should_returnNotifications_when_getAllNotifications() {
        // Arrange
        Notification n = new Notification();
        n.setId(1L);
        n.setBookingId(1L);
        n.setType("CREATED");
        n.setTitle("予約作成完了");
        n.setBody("body");

        List<Notification> list = Arrays.asList(n);
        when(notificationService.getAllNotifications()).thenReturn(list);

        // Act
        List<Notification> result = notificationController.getAllNotifications();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo("CREATED");
    }
}
