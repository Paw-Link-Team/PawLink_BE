package com.gdg.backend.chat.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

import java.time.LocalDateTime;
@Entity
@Table(
        name = "chat_rooms",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_chat_room_board_walker",
                        columnNames = {"board_id", "walker_user_id"}
                )
        }
)
@Getter
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomId;

    private Long boardId;
    private Long ownerUserId;
    private Long walkerUserId;

    @Enumerated(EnumType.STRING)
    private ChatRoomStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected ChatRoom() {}

    public static ChatRoom create(
            Long boardId,
            Long ownerUserId,
            Long walkerUserId
    ) {
        ChatRoom room = new ChatRoom();
        room.boardId = boardId;
        room.ownerUserId = ownerUserId;
        room.walkerUserId = walkerUserId;
        room.status = ChatRoomStatus.ACTIVE;
        room.createdAt = LocalDateTime.now();
        room.updatedAt = LocalDateTime.now();
        return room;
    }
}
