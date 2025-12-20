package com.gdg.backend.chat.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chatRoomId;
    private LocalDate date;
    private LocalTime time;
    private String locationAddress;
    private int reminderMinutesBefore;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
