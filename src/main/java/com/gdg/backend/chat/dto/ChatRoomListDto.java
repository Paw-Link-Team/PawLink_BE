package com.gdg.backend.chat.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ChatRoomListDto {
    private String chatRoomId;
    private String profileImgUrl;
    private String title;
    private String lastMessage;
    private LocalDateTime lastSentAt;
    private int unreadCount;
    private String status;  // ALL, UNREAD, COMPLETED
}
