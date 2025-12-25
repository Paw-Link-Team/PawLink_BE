package com.gdg.backend.walker.walkHistory.repository;

import com.gdg.backend.walker.walkHistory.domain.WalkHistory;
import com.gdg.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalkHistoryRepository extends JpaRepository<WalkHistory, Long> {

    List<WalkHistory> findByUserOrderByStartedAtDesc(User user);
}
