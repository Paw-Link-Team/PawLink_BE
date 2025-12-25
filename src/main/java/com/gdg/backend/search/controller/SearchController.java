package com.gdg.backend.search.controller;

import com.gdg.backend.global.code.SuccessCode;
import com.gdg.backend.global.response.ApiResponse;
import com.gdg.backend.global.security.UserPrincipal;
import com.gdg.backend.search.dto.SearchHistoryResponse;
import com.gdg.backend.search.dto.SearchResponse;
import com.gdg.backend.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<ApiResponse<SearchResponse>> search(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam String keyword
    ) {
        return ApiResponse.success(
                SuccessCode.READ_SUCCESS,
                searchService.search(principal.userId(), keyword)
        );
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<SearchHistoryResponse>>> history(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ApiResponse.success(
                SuccessCode.READ_SUCCESS,
                searchService.getHistory(principal.userId())
        );
    }

    @DeleteMapping("/history/{historyId}")
    public ResponseEntity<ApiResponse<Void>> deleteOne(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long historyId
    ) {
        searchService.deleteHistoryItem(principal.userId(), historyId);
        return ApiResponse.success(SuccessCode.DELETE, null);
    }

    @DeleteMapping("/history")
    public ResponseEntity<ApiResponse<Void>> clear(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        searchService.clearHistory(principal.userId());
        return ApiResponse.success(SuccessCode.DELETE, null);
    }
}
