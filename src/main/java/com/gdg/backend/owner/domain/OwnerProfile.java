package com.gdg.backend.owner.domain;

import com.gdg.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "owner_profile",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "user_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OwnerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int petCount;

    @Column(nullable = false)
    private int reviewCount;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    private OwnerProfile(User user) {
        this.user = user;
        this.petCount = 0;
        this.reviewCount = 0;
        this.startedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void increasePetCount() {
        this.petCount++;
        this.updatedAt = LocalDateTime.now();
    }

    public void decreasePetCount() {
        if (this.petCount > 0) {
            this.petCount--;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void addReview() {
        this.reviewCount++;
        this.updatedAt = LocalDateTime.now();
    }
}
