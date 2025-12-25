package com.gdg.backend.board.domain;

import com.gdg.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "board_interest",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "board_id"})
        }
)
public class BoardInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private BoardInterest(User user, Board board) {
        this.user = user;
        this.board = board;
        this.createdAt = LocalDateTime.now();
    }

    public static BoardInterest create(User user, Board board) {
        return new BoardInterest(user, board);
    }
}
