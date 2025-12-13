package com.gdg.backend.user.repository;

import com.gdg.backend.user.domain.Provider;
import com.gdg.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByProviderAndProviderId(Provider provider, String providerId);
    boolean existsByProviderAndProviderId(Provider provider, String providerId);
}
