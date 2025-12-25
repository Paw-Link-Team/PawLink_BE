package com.gdg.backend.walker.service;

import com.gdg.backend.walker.dto.WalkerProfileResponse;
import com.gdg.backend.walker.dto.WalkerRankResponse;
import com.gdg.backend.walker.domain.WalkerProfile;
import com.gdg.backend.walker.repository.WalkerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalkerQueryService {

    private final WalkerProfileRepository walkerProfileRepository;

    public List<WalkerRankResponse> getRankList(int size) {
        List<WalkerProfile> profiles =
                walkerProfileRepository.findTopRankers(
                        PageRequest.of(0, size)
                );

        List<WalkerRankResponse> result = new ArrayList<>();

        int rank = 1;
        for (WalkerProfile wp : profiles) {
            result.add(WalkerRankResponse.of(
                    rank++,
                    wp.getUser().getId(),
                    wp.getUser().getNickname(),
                    wp.getTotalDistanceKm(),
                    wp.getWalkCount()
            ));
        }

        return result;
    }

    public WalkerProfileResponse getProfile(Long userId) {

        WalkerProfile wp = walkerProfileRepository.findAll().stream()
                .filter(p -> p.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("산책가 없음"));

        return new WalkerProfileResponse(
                wp.getUser().getId(),
                wp.getUser().getNickname(),
                wp.getUser().getLocation(),     // User 필드 기준
                wp.getUser().getPhoneNumber(),
                wp.getAvgRating(),
                wp.getTotalDistanceKm(),
                wp.getWalkCount(),
                wp.getCareerYears(),
                wp.getUser().getProfileImageUrl()
        );
    }

}
