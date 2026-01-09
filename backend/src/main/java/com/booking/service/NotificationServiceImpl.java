package com.booking.service;

import com.booking.model.Booking;
import com.booking.model.Notification;
import com.booking.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public Notification createNotificationForBooking(Booking booking) {
        Notification n = new Notification();
        n.setBookingId(booking.getId());
        n.setType("CREATED");
        n.setTitle("予約作成完了");
        String body = String.format("リソース:%d 開始:%s 終了:%s",
                booking.getResourceId(), booking.getStartTime().format(DF), booking.getEndTime().format(DF));
        n.setBody(body);
        return notificationRepository.save(n);
    }

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
}
