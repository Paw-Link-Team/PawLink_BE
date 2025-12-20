package com.gdg.backend.chat.dto;

import java.time.LocalDateTime;

public class ChatMessageDto {
    private String chatRoomId;
    private Long senderUserId;
    private String senderNickname;
    private String message;
    private LocalDateTime sentAt;
    private boolean read;

    // getter, setter
}
