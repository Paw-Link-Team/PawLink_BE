package com.gdg.backend.auth.naver.repository;

import com.gdg.backend.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NaverRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
