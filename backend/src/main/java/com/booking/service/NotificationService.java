package com.booking.service;

import com.booking.model.Notification;
import com.booking.model.Resource;
import com.booking.repository.NotificationRepository;
import com.booking.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final ResourceRepository resourceRepository;

    public Notification createBookingNotification(Long bookingId, String title, String type, java.time.LocalDateTime startAt, java.time.LocalDateTime endAt, Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId).orElse(null);
        Notification notification = new Notification(bookingId, title, type, startAt, endAt, resource);
        return notificationRepository.save(notification);
    }
    public List<Notification> getNotificationsByBookingId(Long bookingId) {
        return notificationRepository.findByBookingId(bookingId);
    }

    @Transactional(readOnly = true)
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
}
