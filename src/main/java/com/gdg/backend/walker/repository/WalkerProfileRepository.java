package com.gdg.backend.walker.repository;

import com.gdg.backend.user.domain.User;
import com.gdg.backend.walker.domain.WalkerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface WalkerProfileRepository extends JpaRepository<WalkerProfile, Long> {

    @Query("""
        select wp
        from WalkerProfile wp
        order by wp.totalDistanceKm desc
    """)
    List<WalkerProfile> findTopRankers(Pageable pageable);

    Optional<WalkerProfile> findByUser(User user);
    Optional<WalkerProfile> findByUserId(Long id);
}
