package com.gdg.backend.chat.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chatRoomId;

    private LocalDate date;
    private LocalTime time;
    private String locationAddress;
    private int reminderMinutesBefore;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 생성자, getter/setter
}
