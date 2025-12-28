package com.gdg.backend.walk.session.repository;

import com.gdg.backend.user.domain.User;
import com.gdg.backend.walk.session.domain.WalkSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalkSessionRepository
        extends JpaRepository<WalkSession, Long> {


    Optional<WalkSession> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

}
