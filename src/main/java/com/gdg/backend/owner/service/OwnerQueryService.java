package com.gdg.backend.owner.service;

import com.gdg.backend.owner.domain.OwnerProfile;
import com.gdg.backend.owner.dto.OwnerProfileResponse;
import com.gdg.backend.owner.repository.OwnerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OwnerQueryService {

    private final OwnerProfileRepository ownerProfileRepository;

    public OwnerProfileResponse getProfile(Long userId) {
        OwnerProfile profile =
                ownerProfileRepository.findByUserId(userId)
                        .orElseThrow(() -> new IllegalArgumentException("보호자 프로필 없음"));

        return new OwnerProfileResponse(
                profile.getUser().getId(),
                profile.getUser().getNickname(),
                profile.getUser().getProfileImageUrl(),
                profile.getPetCount(),
                profile.getReviewCount(),
                profile.getStartedAt()
        );
    }
}
