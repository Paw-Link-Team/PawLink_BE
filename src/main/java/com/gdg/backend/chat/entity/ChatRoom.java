package com.gdg.backend.chat.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_rooms")
@Getter
@Setter
public class ChatRoom {

    @Id
    private String chatRoomId;

    private Long ownerUserId;
    private Long walkerUserId;

    @Enumerated(EnumType.STRING)
    private ChatRoomStatus status; // ALL, UNREAD, COMPLETED 구분

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
