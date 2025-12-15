package com.gdg.backend.admin.controller;

import com.gdg.backend.admin.dto.AdminSignupRequest;
import com.gdg.backend.admin.service.AdminService;
import com.gdg.backend.global.code.SuccessCode;
import com.gdg.backend.global.response.ApiResponse;
import com.gdg.backend.user.dto.TokenResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('SUPER_ADMIN')")
@Tag(name = "관리자")
public class AdminController {

    private final AdminService adminService;

    @PatchMapping("/admin")
    @Operation(summary = "관리자 생성", description = "SUPER_ADMIN만 접속하여 관리자를 생성할 수 있습니다.")
    public ResponseEntity<ApiResponse<Object>> createAdmin(
            @RequestBody AdminSignupRequest request
    ){
            TokenResponseDto token = adminService.createAdmin(request);

            return ApiResponse.success(SuccessCode.ADMIN_CREATED, token);
    }
}
