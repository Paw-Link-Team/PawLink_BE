package com.gdg.backend.chat.dto;

import java.time.LocalDateTime;

public class ChatRoomListDto {
    private String chatRoomId;
    private String profileImgUrl;
    private String title;
    private String lastMessage;
    private LocalDateTime lastSentAt;
    private int unreadCount;
    private String status;  // ALL, UNREAD, COMPLETED

    // getter, setter
}
