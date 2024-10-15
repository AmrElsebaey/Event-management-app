package com.spring_boot.task_management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data @NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "summary")
    private String summary;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "location")
    private String location;

    @Column(name = "description")
    private String description;

    @Column(name = "google_id")
    private String googleId;

    public Event(String summary, LocalDateTime startTime, LocalDateTime endTime, String location, String description) {
        this.summary = summary;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.description = description;
    }
}

