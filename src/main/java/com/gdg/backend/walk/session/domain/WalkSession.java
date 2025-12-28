package com.gdg.backend.walk.session.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

import java.time.LocalDateTime;
@Entity
@Table(
        name = "walk_sessions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_walk_session_user",
                        columnNames = {"user_id"}
                )
        }
)
@Getter
public class WalkSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    protected WalkSession() {}

    /* =====================
     * 생성 팩토리
     * ===================== */
    public static WalkSession start(Long userId) {
        WalkSession session = new WalkSession();
        session.userId = userId;
        session.startedAt = LocalDateTime.now();
        return session;
    }
}
