package com.gdg.backend.chat.dto;

public class ChatRoomDetailDto {
    private String chatRoomId;
    private String profileName;
    private String profilePhone;
    private PostDto post;          // 게시글 이미지, 제목, 예약시간, 장소 포함
    private AppointmentDto appointment;  // 예약 정보 (옵션)

    // getter, setter
}
