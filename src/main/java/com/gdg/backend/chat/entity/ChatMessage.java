package com.gdg.backend.chat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chatRoomId;
    private Long senderUserId;
    private String senderNickname;

    @Column(columnDefinition = "TEXT")
    private String message;

    private LocalDateTime sentAt;
    private boolean read;

    // 생성자, getter/setter
}
