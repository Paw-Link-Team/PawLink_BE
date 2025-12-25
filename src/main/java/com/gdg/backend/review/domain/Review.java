package com.gdg.backend.review.domain;

import com.gdg.backend.user.domain.User;
import com.gdg.backend.walker.domain.WalkerProfile;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "walker_profile_id", nullable = false)
    private WalkerProfile walkerProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    private Review(
            WalkerProfile walkerProfile,
            User writer,
            int rating,
            String content
    ) {
        this.walkerProfile = walkerProfile;
        this.writer = writer;
        this.rating = rating;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public void update(int rating, String content){
        this.rating = rating;
        this.content = content;
    }

}
