package com.booking.config;

import com.booking.model.Booking;
import com.booking.model.BookingStatus;
import com.booking.model.Resource;
import com.booking.repository.BookingRepository;
import com.booking.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Data initializer to populate sample data on application startup
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ResourceRepository resourceRepository;
    private final BookingRepository bookingRepository;

    @Override
    public void run(String... args) {
        // Check if data already exists
        if (resourceRepository.count() > 0) {
            return;
        }

        // Create sample resources
        Resource meetingRoom1 = new Resource();
        meetingRoom1.setName("会議室A");
        meetingRoom1.setDescription("大型会議室、プロジェクター完備");
        meetingRoom1.setCapacity(10);
        meetingRoom1.setAvailable(true);
        resourceRepository.save(meetingRoom1);

        Resource meetingRoom2 = new Resource();
        meetingRoom2.setName("会議室B");
        meetingRoom2.setDescription("小型会議室、ホワイトボード完備");
        meetingRoom2.setCapacity(6);
        meetingRoom2.setAvailable(true);
        resourceRepository.save(meetingRoom2);

        Resource meetingRoom3 = new Resource();
        meetingRoom3.setName("会議室C");
        meetingRoom3.setDescription("中型会議室、TV会議システム完備");
        meetingRoom3.setCapacity(8);
        meetingRoom3.setAvailable(true);
        resourceRepository.save(meetingRoom3);

        Resource lab = new Resource();
        lab.setName("実験室1");
        lab.setDescription("研究用実験室");
        lab.setCapacity(4);
        lab.setAvailable(true);
        resourceRepository.save(lab);

        Resource studio = new Resource();
        studio.setName("スタジオ");
        studio.setDescription("録音・録画スタジオ");
        studio.setCapacity(5);
        studio.setAvailable(false); // Temporarily unavailable
        resourceRepository.save(studio);

        // Create sample bookings
        LocalDateTime now = LocalDateTime.now();
        
        Booking booking1 = new Booking();
        booking1.setResourceId(meetingRoom1.getId());
        booking1.setCustomerName("田中太郎");
        booking1.setCustomerEmail("tanaka@example.com");
        booking1.setStartTime(now.plusDays(1).withHour(10).withMinute(0));
        booking1.setEndTime(now.plusDays(1).withHour(11).withMinute(30));
        booking1.setStatus(BookingStatus.CONFIRMED);
        booking1.setNotes("プロジェクト定例会議");
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setResourceId(meetingRoom1.getId());
        booking2.setCustomerName("佐藤花子");
        booking2.setCustomerEmail("sato@example.com");
        booking2.setStartTime(now.plusDays(1).withHour(14).withMinute(0));
        booking2.setEndTime(now.plusDays(1).withHour(16).withMinute(0));
        booking2.setStatus(BookingStatus.CONFIRMED);
        booking2.setNotes("クライアントミーティング");
        bookingRepository.save(booking2);

        Booking booking3 = new Booking();
        booking3.setResourceId(meetingRoom2.getId());
        booking3.setCustomerName("鈴木一郎");
        booking3.setCustomerEmail("suzuki@example.com");
        booking3.setStartTime(now.plusDays(2).withHour(9).withMinute(0));
        booking3.setEndTime(now.plusDays(2).withHour(10).withMinute(0));
        booking3.setStatus(BookingStatus.PENDING);
        booking3.setNotes("チームミーティング");
        bookingRepository.save(booking3);

        Booking booking4 = new Booking();
        booking4.setResourceId(meetingRoom3.getId());
        booking4.setCustomerName("高橋美咲");
        booking4.setCustomerEmail("takahashi@example.com");
        booking4.setStartTime(now.plusDays(3).withHour(13).withMinute(0));
        booking4.setEndTime(now.plusDays(3).withHour(15).withMinute(0));
        booking4.setStatus(BookingStatus.CONFIRMED);
        booking4.setNotes("リモート会議");
        bookingRepository.save(booking4);

        Booking booking5 = new Booking();
        booking5.setResourceId(lab.getId());
        booking5.setCustomerName("山田健太");
        booking5.setCustomerEmail("yamada@example.com");
        booking5.setStartTime(now.plusDays(1).withHour(15).withMinute(0));
        booking5.setEndTime(now.plusDays(1).withHour(18).withMinute(0));
        booking5.setStatus(BookingStatus.CONFIRMED);
        booking5.setNotes("実験作業");
        bookingRepository.save(booking5);

        System.out.println("Sample data initialized successfully!");
    }
}

