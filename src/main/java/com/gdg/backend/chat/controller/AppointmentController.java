package com.gdg.backend.chat.controller;

import com.gdg.backend.chat.dto.AppointmentDto;
import com.gdg.backend.chat.service.AppointmentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/{chatRoomId}")
    public void saveAppointment(@PathVariable Long chatRoomId, @RequestBody AppointmentDto dto) {
        appointmentService.saveOrUpdate(chatRoomId, dto);
    }

    @GetMapping("/{chatRoomId}")
    public AppointmentDto getAppointment(@PathVariable Long chatRoomId) {
        return appointmentService.getByRoomId(chatRoomId);
    }
}
