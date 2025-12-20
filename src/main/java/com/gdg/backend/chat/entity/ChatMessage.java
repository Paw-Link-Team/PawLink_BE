package com.gdg.backend.chat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatRoomId;
    private Long senderUserId;
    private String senderNickname;

    @Column(columnDefinition = "TEXT")
    private String message;

    private LocalDateTime sentAt;
    
    @Column(name = "is_read")
    private boolean isRead;
}
