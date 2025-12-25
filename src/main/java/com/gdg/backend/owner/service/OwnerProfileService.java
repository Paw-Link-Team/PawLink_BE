package com.gdg.backend.owner.service;

import com.gdg.backend.owner.domain.OwnerProfile;
import com.gdg.backend.owner.repository.OwnerProfileRepository;
import com.gdg.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OwnerProfileService {

    private final OwnerProfileRepository ownerProfileRepository;

    @Transactional
    public OwnerProfile createIfAbsent(User user) {
        return ownerProfileRepository
                .findByUser(user)
                .orElseGet(() ->
                        ownerProfileRepository.save(
                                OwnerProfile.builder()
                                        .user(user)
                                        .build()
                        )
                );
    }
}
