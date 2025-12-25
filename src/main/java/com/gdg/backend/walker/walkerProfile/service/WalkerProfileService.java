package com.gdg.backend.walker.walkerProfile.service;

import com.gdg.backend.user.domain.User;
import com.gdg.backend.walker.domain.WalkerProfile;
import com.gdg.backend.walker.repository.WalkerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalkerProfileService {

    private final WalkerProfileRepository walkerProfileRepository;

    @Transactional
    public WalkerProfile createIfAbsent(User user) {
        return walkerProfileRepository
                .findByUser(user)
                .orElseGet(() -> walkerProfileRepository.save(
                        WalkerProfile.builder()
                                .user(user)
                                .build()
                ));
    }
}

