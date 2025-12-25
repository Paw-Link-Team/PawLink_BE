package com.gdg.backend.pet.controller;

import com.gdg.backend.global.code.SuccessCode;
import com.gdg.backend.global.exception.UnauthorizedException;
import com.gdg.backend.global.response.ApiResponse;
import com.gdg.backend.global.security.UserPrincipal;
import com.gdg.backend.pet.dto.PetDetailResponse;
import com.gdg.backend.pet.dto.PetRequestDto;
import com.gdg.backend.pet.dto.PetResponseDto;
import com.gdg.backend.pet.service.PetService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "pet 정보 컨트롤러")
@RestController
@RequestMapping("/api/pet")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<PetResponseDto>> createPet(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody PetRequestDto request
    ){
        Long userId = principal.userId();

        return ApiResponse.success(SuccessCode.PET_CREATED, petService.setPet(userId, request));
    }

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<List<PetResponseDto>>> getPetInfo(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        if (principal == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        Long userId = principal.userId();
        return ApiResponse.success(
                SuccessCode.OK,
                petService.petInfo(userId)
        );
    }


    @GetMapping("/info/{petId}")
    public ResponseEntity<ApiResponse<PetDetailResponse>>getPet(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long petId
    ) {
        return ApiResponse.success(
                SuccessCode.OK,
                petService.getPet(user.userId(), petId)
        );
    }

    @PatchMapping("/update/{petId}")
    public ResponseEntity<ApiResponse<PetResponseDto>> updatePet(
            @PathVariable Long petId,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody PetRequestDto request
    ) {
        Long userId = principal.userId();
        return ApiResponse.success(
                SuccessCode.OK,
                petService.updatePet(userId, petId, request)
        );
    }


    @DeleteMapping("/delete/{petId}")
    public ResponseEntity<ApiResponse<Void>> deletePet(
            @PathVariable Long petId,
            @AuthenticationPrincipal UserPrincipal principal
            ) {
        petService.deletePet(principal.userId(), petId);
        return ApiResponse.success(SuccessCode.OK, null);
    }

    @PatchMapping("/{petId}/representative")
    public ResponseEntity<ApiResponse<Void>> setRepresentative(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long petId
    ) {
        petService.setRepresentativePet(principal.userId(), petId);
        return ApiResponse.success(SuccessCode.OK,null);
    }

}
