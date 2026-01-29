package com.booking.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long bookingId;
    private String title;
    private String type; // 例: "CREATED"
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Resource resource;

    public Notification() {}

    public Notification(Long bookingId, String title, String type, LocalDateTime startAt, LocalDateTime endAt, Resource resource) {
        this.bookingId = bookingId;
        this.title = title;
        this.type = type;
        this.startAt = startAt;
        this.endAt = endAt;
        this.resource = resource;
    }

    public Long getId() { return id; }
    public Long getBookingId() { return bookingId; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public LocalDateTime getStartAt() { return startAt; }
    public LocalDateTime getEndAt() { return endAt; }
    public Resource getResource() { return resource; }

    public void setId(Long id) { this.id = id; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public void setTitle(String title) { this.title = title; }
    public void setType(String type) { this.type = type; }
    public void setStartAt(LocalDateTime startAt) { this.startAt = startAt; }
    public void setEndAt(LocalDateTime endAt) { this.endAt = endAt; }
    public void setResource(Resource resource) { this.resource = resource; }
}
