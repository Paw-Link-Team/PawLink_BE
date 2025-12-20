package com.gdg.backend.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomDetailDto {
    private Long chatRoomId;
    private String profileName;
    private String profilePhone;
    // PostDto가 없으므로 임시로 주석 처리하거나 필요한 필드만 직접 정의
    // private PostDto post;          // 게시글 이미지, 제목, 예약시간, 장소 포함
    private AppointmentDto appointment;  // 예약 정보 (옵션)
}
