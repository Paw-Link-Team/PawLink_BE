package com.gdg.backend.search.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "search_history",
        indexes = {
                @Index(name = "idx_sh_user_created", columnList = "userId, createdAt"),
                @Index(name = "idx_sh_user_keyword", columnList = "userId, keyword")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String keyword;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public static SearchHistory of(Long userId, String keyword) {
        return SearchHistory.builder()
                .userId(userId)
                .keyword(keyword)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void touchNow() {
        this.createdAt = LocalDateTime.now();
    }
}
