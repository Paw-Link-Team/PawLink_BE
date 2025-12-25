package com.gdg.backend.walker.walkerProfile.service;

import com.gdg.backend.user.domain.User;
import com.gdg.backend.walker.domain.WalkerProfile;
import com.gdg.backend.walker.repository.WalkerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalkerProfileService {

    private final WalkerProfileRepository walkerProfileRepository;

    @Transactional
    public void addWalk(User user, BigDecimal distanceKm) {

        WalkerProfile profile = walkerProfileRepository
                .findByUser(user)
                .orElseGet(() ->
                        walkerProfileRepository.save(
                                WalkerProfile.builder()
                                        .user(user)
                                        .build()
                        )
                );

        profile.addWalk(distanceKm);
    }

    @Transactional(readOnly = true)
    public WalkerProfile getProfile(User user) {
        return walkerProfileRepository.findByUser(user)
                .orElseThrow(() ->
                        new IllegalStateException("WalkerProfile이 없습니다.")
                );
    }
}
