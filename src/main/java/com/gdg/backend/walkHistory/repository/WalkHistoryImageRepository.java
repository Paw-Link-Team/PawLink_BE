package com.gdg.backend.walkHistory.repository;

import com.gdg.backend.walkHistory.domain.WalkHistoryImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalkHistoryImageRepository
        extends JpaRepository<WalkHistoryImage, Long> {

    List<WalkHistoryImage> findByWalkHistoryId(Long walkHistoryId);
    void deleteByWalkHistoryId(Long walkHistoryId);
}
