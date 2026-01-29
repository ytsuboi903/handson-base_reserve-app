package com.booking.repository;

import com.booking.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
	List<Notification> findByBookingId(Long bookingId);
}
