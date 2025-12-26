package com.gdg.backend.chat.controller;

import com.gdg.backend.chat.dto.AppointmentDto;
import com.gdg.backend.chat.service.AppointmentService;
import com.gdg.backend.global.code.SuccessCode;
import com.gdg.backend.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/{chatRoomId}")
    public ResponseEntity<ApiResponse<Object>> saveAppointment(@PathVariable Long chatRoomId, @RequestBody AppointmentDto dto) {
        appointmentService.saveOrUpdate(chatRoomId, dto);
        return ApiResponse.success(SuccessCode.PROCESS_SUCCESS);
    }

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<ApiResponse<AppointmentDto>> getAppointment(@PathVariable Long chatRoomId) {
        return ApiResponse.success(SuccessCode.READ_SUCCESS, appointmentService.getByRoomId(chatRoomId));
    }
}
