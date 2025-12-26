package com.gdg.backend.walkHistory.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "walk_history_image")
public class WalkHistoryImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long walkHistoryId;

    @Column(nullable = false, length = 500)
    private String imageUrl;

    private WalkHistoryImage(Long walkHistoryId, String imageUrl) {
        this.walkHistoryId = walkHistoryId;
        this.imageUrl = imageUrl;
    }

    public static WalkHistoryImage of(Long walkHistoryId, String imageUrl) {
        return new WalkHistoryImage(walkHistoryId, imageUrl);
    }
}
