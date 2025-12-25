package com.gdg.backend.chat.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class AppointmentDto {
    private LocalDate date;
    private LocalTime time;
    private String locationAddress; // 도로명 주소
    private int reminderMinutesBefore;
}
