package com.booking.controller;

import com.booking.model.Notification;
import com.booking.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    /**
     * 通知一覧取得
     */
    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    /**
     * 予約IDで通知取得
     */
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<Notification>> getNotificationsByBookingId(@PathVariable Long bookingId) {
        List<Notification> notifications = notificationService.getNotificationsByBookingId(bookingId);
        return ResponseEntity.ok(notifications);
    }
}
