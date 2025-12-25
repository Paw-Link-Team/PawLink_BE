package com.gdg.backend.owner.controller;

import com.gdg.backend.owner.dto.OwnerProfileResponse;
import com.gdg.backend.owner.service.OwnerQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/owners")
public class OwnerController {

    private final OwnerQueryService ownerQueryService;

    @GetMapping("/{userId}")
    public OwnerProfileResponse getOwnerProfile(
            @PathVariable Long userId
    ) {
        return ownerQueryService.getProfile(userId);
    }
}
