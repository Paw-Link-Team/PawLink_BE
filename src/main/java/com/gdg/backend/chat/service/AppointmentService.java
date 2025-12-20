package com.gdg.backend.chat.service;

import com.gdg.backend.chat.dto.AppointmentDto;
import com.gdg.backend.chat.entity.Appointment;
import com.gdg.backend.chat.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public void saveOrUpdate(Long chatRoomId, AppointmentDto dto) {
        Appointment appt = appointmentRepository.findByChatRoomId(chatRoomId).orElse(new Appointment());
        appt.setChatRoomId(chatRoomId);
        appt.setDate(dto.getDate());
        appt.setTime(dto.getTime());
        appt.setLocationAddress(dto.getLocationAddress());
        appt.setReminderMinutesBefore(dto.getReminderMinutesBefore());
        appt.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appt);
    }

    public AppointmentDto getByRoomId(Long chatRoomId) {
        return appointmentRepository.findByChatRoomId(chatRoomId)
                .map(this::toDto)
                .orElse(null);
    }

    private AppointmentDto toDto(Appointment appt) {
        AppointmentDto dto = new AppointmentDto();
        dto.setDate(appt.getDate());
        dto.setTime(appt.getTime());
        dto.setLocationAddress(appt.getLocationAddress());
        dto.setReminderMinutesBefore(appt.getReminderMinutesBefore());
        return dto;
    }
}
