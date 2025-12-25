package com.gdg.backend.walker.controller;

import com.gdg.backend.walker.dto.WalkerProfileResponse;
import com.gdg.backend.walker.dto.WalkerRankResponse;
import com.gdg.backend.walker.service.WalkerQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/walkers")
public class WalkerController {

    private final WalkerQueryService walkerQueryService;

    @GetMapping("/rank")
    public List<WalkerRankResponse> getWalkerRank(
            @RequestParam(defaultValue = "50") int size
    ) {
        return walkerQueryService.getRankList(size);
    }

    @GetMapping("/{userId}")
    public WalkerProfileResponse getWalkerProfile(
            @PathVariable Long userId
    ) {
        return walkerQueryService.getProfile(userId);
    }
}
