package com.gdg.backend.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessagePayload {
    private Long chatRoomId;
    private Long senderUserId;
    private String senderNickname;
    private String message;
}
