package com.booking.service;

import com.booking.model.Booking;
import com.booking.model.Notification;

import java.util.List;

public interface NotificationService {
    Notification createNotificationForBooking(Booking booking);
    List<Notification> getAllNotifications();
}
