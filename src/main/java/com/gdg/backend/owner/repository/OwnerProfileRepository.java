package com.gdg.backend.owner.repository;

import com.gdg.backend.owner.domain.OwnerProfile;
import com.gdg.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OwnerProfileRepository extends JpaRepository<OwnerProfile, Long> {

    Optional<OwnerProfile> findByUser(User user);
    Optional<OwnerProfile> findByUserId(Long userId);
}
