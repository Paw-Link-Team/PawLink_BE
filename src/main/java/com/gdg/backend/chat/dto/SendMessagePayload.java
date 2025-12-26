package com.gdg.backend.chat.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 클라이언트 → 서버 메시지 전송용 DTO
 */
@Getter
@Setter
public class SendMessagePayload {
    private Long chatRoomId;
    private String message;
}

