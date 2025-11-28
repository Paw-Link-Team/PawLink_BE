package com.gdg.backend.auth.repository;

import com.gdg.backend.auth.domain.Provider;
import com.gdg.backend.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByProviderAndProviderId(Provider provider, Long providerId);
}
