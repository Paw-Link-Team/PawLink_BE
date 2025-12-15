package com.gdg.backend.admin.controller;

import com.gdg.backend.admin.dto.AdminSignupRequest;
import com.gdg.backend.admin.service.AdminService;
import com.gdg.backend.global.code.SuccessCode;
import com.gdg.backend.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole=('SUPER_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public ResponseEntity<ApiResponse<Object>> createAdmin(
            @RequestBody AdminSignupRequest request
    ){
            adminService.createAdmin(request);

            return ApiResponse.success(SuccessCode.ADMIN_CREATED);
    }
}
