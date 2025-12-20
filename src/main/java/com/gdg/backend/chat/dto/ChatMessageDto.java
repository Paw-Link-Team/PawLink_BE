package com.gdg.backend.chat.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMessageDto {
    private String chatRoomId;
    private Long senderUserId;
    private String senderNickname;
    private String message;
    private LocalDateTime sentAt;
    private boolean read;
}
